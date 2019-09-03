package com.jmatio.io;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.jmatio.common.MatDataTypes;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLNumericArray;
import com.jmatio.types.MLSparse;
import com.jmatio.types.MLStructure;

/**
 * MAT-file writer.
 * 
 * Usage:
 * <pre><code>
 * //1. First create example arrays
 * double[] src = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 };
 * MLDouble mlDouble = new MLDouble( "double_arr", src, 3 );
 * MLChar mlChar = new MLChar( "char_arr", "I am dummy" );
 *         
 * //2. write arrays to file
 * ArrayList<MLArray> list = new ArrayList<MLArray>();
 * list.add( mlDouble );
 * list.add( mlChar );
 * 
 * new MatFileWriter( "mat_file.mat", list );
 * </code></pre>
 * 
 * this is "equal" to Matlab commands:
 * <pre><code>
 * >> double_arr = [ 1 2; 3 4; 5 6];
 * >> char_arr = 'I am dummy';
 * >>
 * >> save('mat_file.mat', 'double_arr', 'char_arr');
 * </pre></code>
 * 
 * @author Wojciech Gradkowski (<a href="mailto:wgradkowski@gmail.com">wgradkowski@gmail.com</a>)
 */
public class MatFileWriter
{
//    private static final Logger logger = Logger.getLogger(MatFileWriter.class);
    
    /**
     * Writes MLArrays into file given by <code>fileName</code>.
     * 
     * @param fileName - name of ouput file
     * @param data - <code>Collection</code> of <code>MLArray</code> elements
     * @throws IOException
     * @throws DataFormatException
     */
    public MatFileWriter(String fileName, Collection<MLArray> data) throws IOException
    {
        this( new File(fileName), data );
    }
    /**
     * Writes MLArrays into <code>File</code>.
     * 
     * @param file - an output <code>File</code>
     * @param data - <code>Collection</code> of <code>MLArray</code> elements
     * @throws IOException
     * @throws DataFormatException
     */
    public MatFileWriter(File file, Collection<MLArray> data) throws IOException
    {
        this( new FileOutputStream(file).getChannel(), data );
    }
    /**
     * Writes MLArrays into <code>OuputSteram</code>.
     * 
     * Writes MAT-file header and compressed data (<code>miCOMPRESSED</code>).
     * 
     * @param output - <code>OutputStream</code>
     * @param data - <code>Collection</code> of <code>MLArray</code> elements
     * @throws IOException
     */
    public MatFileWriter(WritableByteChannel channel, Collection<MLArray> data) throws IOException
    {
        //write header
        writeHeader(channel);
        
        //write data
        for ( MLArray matrix : data )
        {
            //prepare buffer for MATRIX data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream( baos );
            //write MATRIX bytes into buffer
            writeMatrix( dos, matrix );
            
            //compress data to save storage
            Deflater compresser = new Deflater();
            
            byte[] input = baos.toByteArray();
            
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new DeflaterOutputStream(compressed, compresser));
            
            dout.write(input);
            
            dout.close();
            compressed.close();
            
            //write COMPRESSED tag and compressed data into output channel
            byte[] compressedBytes = compressed.toByteArray();
            ByteBuffer buf = ByteBuffer.allocateDirect(2 * 4 /* Int size */ + compressedBytes.length);
            buf.putInt( MatDataTypes.miCOMPRESSED );
            buf.putInt( compressedBytes.length );
            buf.put( compressedBytes );
            
            buf.flip();
            channel.write( buf );
        }
        
        channel.close();        
    }
    
    /**
     * Writes MAT-file header into <code>OutputStream</code>
     * @param os <code>OutputStream</code>
     * @throws IOException
     */
    private void writeHeader(WritableByteChannel channel) throws IOException
    {
        //write descriptive text
        MatFileHeader header = MatFileHeader.createHeader();
        char[] dest = new char[116];
        char[] src = header.getDescription().toCharArray();
        System.arraycopy(src, 0, dest, 0, src.length);
        
        byte[] endianIndicator = header.getEndianIndicator();
        
        ByteBuffer buf = ByteBuffer.allocateDirect(dest.length * 2 /* Char size */ + 2 + endianIndicator.length);
        
        for ( int i = 0; i < dest.length; i++ )
        {
            buf.put( (byte)dest[i] );
        }
        //write subsyst data offset
        buf.position( buf.position() + 8);
        
        //write version
        int version = header.getVersion();
        buf.put( (byte)(version >> 8) );
        buf.put( (byte)version );
        
        buf.put( endianIndicator );
        
        buf.flip();
        channel.write(buf);
    }
    
    /**
     * Writes MATRIX into <code>OutputStream</code>.
     * 
     * @param os - <code>OutputStream</code>
     * @param array - a <code>MLArray</code>
     * @throws IOException
     */
    private void writeMatrix(DataOutputStream output, MLArray array) throws IOException
    {   
        OSArrayTag tag;
        ByteArrayOutputStream buffer;         
        DataOutputStream bufferDOS;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        //flags
        writeFlags(dos, array);

        //dimensions
        writeDimensions(dos, array);
        
        //array name
        writeName(dos, array);
        
        switch ( array.getType() )
        {
            case MLArray.mxCHAR_CLASS:
                //write char data
                buffer = new ByteArrayOutputStream();
                bufferDOS = new DataOutputStream(buffer);
                Character[] ac = ((MLChar)array).exportChar();
                for ( int i = 0; i < ac.length; i++ )
                {
                    bufferDOS.writeByte( (byte)ac[i].charValue() );
                }
                tag = new OSArrayTag(MatDataTypes.miUTF8, buffer.toByteArray() );
                tag.writeTo( dos );
                
                break;
            case MLArray.mxDOUBLE_CLASS:
                
                tag = new OSArrayTag(MatDataTypes.miDOUBLE, 
                                ((MLNumericArray)array).getRealByteBuffer() );
                tag.writeTo( dos );
                
                //write real imaginary
                if ( array.isComplex() )
                {
                    tag = new OSArrayTag(MatDataTypes.miDOUBLE, 
                            ((MLNumericArray)array).getImaginaryByteBuffer() );
                    tag.writeTo( dos );
                }
                break;
            case MLArray.mxUINT8_CLASS:
                
                tag = new OSArrayTag(MatDataTypes.miUINT8, 
                        ((MLNumericArray)array).getRealByteBuffer() );
                tag.writeTo( dos );
                
                //write real imaginary
                if ( array.isComplex() )
                {
                    tag = new OSArrayTag(MatDataTypes.miUINT8, 
                            ((MLNumericArray)array).getImaginaryByteBuffer() );
                    tag.writeTo( dos );
                }
                break;
            case MLArray.mxSTRUCT_CLASS:
                //field name length
                int itag = 4 << 16 | MatDataTypes.miINT32 & 0xffff;
                dos.writeInt( itag );
                dos.writeInt( ((MLStructure)array).getMaxFieldLenth() );
                
                //get field names
                tag = new OSArrayTag(MatDataTypes.miINT8, ((MLStructure)array).getKeySetToByteArray() );
                tag.writeTo( dos );

                for ( MLArray a : ((MLStructure)array).getAllFields() )
                {
                    writeMatrix(dos, a);
                }
                break;
            case MLArray.mxCELL_CLASS:
                for ( MLArray a : ((MLCell)array).cells() )
                {
                    writeMatrix(dos, a);
                }
                break;
            case MLArray.mxSPARSE_CLASS:
                int[] ai;
                //write ir
                buffer = new ByteArrayOutputStream();
                bufferDOS = new DataOutputStream(buffer);
                ai = ((MLSparse)array).getIR();
                for ( int i : ai )
                {
                        bufferDOS.writeInt( i );
                }
                tag = new OSArrayTag(MatDataTypes.miINT32, buffer.toByteArray() );
                tag.writeTo( dos );
                //write jc
                buffer = new ByteArrayOutputStream();
                bufferDOS = new DataOutputStream(buffer);
                ai = ((MLSparse)array).getJC();
                for ( int i : ai )
                {
                        bufferDOS.writeInt( i );
                }
                tag = new OSArrayTag(MatDataTypes.miINT32, buffer.toByteArray() );
                tag.writeTo( dos );
                //write real
                buffer = new ByteArrayOutputStream();
                bufferDOS = new DataOutputStream(buffer);
                
                Double[] ad = ((MLSparse)array).exportReal();
                
                for ( int i = 0; i < ad.length; i++ )
                {
                    bufferDOS.writeDouble( ad[i].doubleValue() );
                }
                
                tag = new OSArrayTag(MatDataTypes.miDOUBLE, buffer.toByteArray() );
                tag.writeTo( dos );
                //write real imaginary
                if ( array.isComplex() )
                {
                    buffer = new ByteArrayOutputStream();
                    bufferDOS = new DataOutputStream(buffer);
                    ad = ((MLSparse)array).exportImaginary();
                    for ( int i = 0; i < ad.length; i++ )
                    {
                        bufferDOS.writeDouble( ad[i].doubleValue() );
                    }
                    tag = new OSArrayTag(MatDataTypes.miDOUBLE, buffer.toByteArray() );
                    tag.writeTo( dos );
                }
                break;
            default:
                throw new MatlabIOException("Cannot write matrix of type: " + MLArray.typeToString( array.getType() ));
                
        }
        
        
        //write matrix
        output.writeInt(MatDataTypes.miMATRIX); //matrix tag
        output.writeInt( baos.size() ); //size of matrix
        output.write( baos.toByteArray() ); //matrix data
    }
    
    /**
     * Writes MATRIX flags into <code>OutputStream</code>.
     * 
     * @param os - <code>OutputStream</code>
     * @param array - a <code>MLArray</code>
     * @throws IOException
     */
    private void writeFlags(DataOutputStream os, MLArray array) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream bufferDOS = new DataOutputStream(buffer);

        bufferDOS.writeInt( array.getFlags() );
        
        if ( array.isSparse() )
        {
            bufferDOS.writeInt( ((MLSparse)array).getMaxNZ() );
        }
        else
        {
            bufferDOS.writeInt( 0 );
        }
        OSArrayTag tag = new OSArrayTag(MatDataTypes.miUINT32, buffer.toByteArray() );
        tag.writeTo( os );
        
    }
    
    /**
     * Writes MATRIX dimensions into <code>OutputStream</code>.
     * 
     * @param os - <code>OutputStream</code>
     * @param array - a <code>MLArray</code>
     * @throws IOException
     */
    private void writeDimensions(DataOutputStream os, MLArray array) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream bufferDOS = new DataOutputStream(buffer);
        
        int[] dims = array.getDimensions();
        for ( int i = 0; i < dims.length; i++ )
        {
            bufferDOS.writeInt(dims[i]);
        }
        OSArrayTag tag = new OSArrayTag(MatDataTypes.miUINT32, buffer.toByteArray() );
        tag.writeTo( os );
        
    }
    
    /**
     * Writes MATRIX name into <code>OutputStream</code>.
     * 
     * @param os - <code>OutputStream</code>
     * @param array - a <code>MLArray</code>
     * @throws IOException
     */
    private void writeName(DataOutputStream os, MLArray array) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream bufferDOS = new DataOutputStream(buffer);

        byte[] nameByteArray = array.getNameToByteArray();
        buffer = new ByteArrayOutputStream();
        bufferDOS = new DataOutputStream(buffer);
        bufferDOS.write( nameByteArray );
        OSArrayTag tag = new OSArrayTag(16, buffer.toByteArray() );
        tag.writeTo( os );
    }
    
    /**
     * Tiny class that represents MAT-file TAG 
     * It simplifies writing data. Automates writing padding for instance.
     */
    private class OSArrayTag extends MatTag
    {
        private ByteBuffer data;
        private int padding;
        /**
         * Creates TAG and stets its <code>size</code> as size of byte array
         * 
         * @param type
         * @param data
         */
        public OSArrayTag(int type, byte[] data )
        {
            this ( type, ByteBuffer.wrap( data ) );
        }
        /**
         * Creates TAG and stets its <code>size</code> as size of byte array
         * 
         * @param type
         * @param data
         */
        public OSArrayTag(int type, ByteBuffer data )
        {
            super( type, data.limit() );
            this.data = data;
            data.rewind();
            this.padding = getPadding(data.limit(), false);
        }

        
        /**
         * Writes tag and data to <code>DataOutputStream</code>. Wites padding if neccesary.
         * 
         * @param os
         * @throws IOException
         */
        public void writeTo(DataOutputStream os) throws IOException
        {
            os.writeInt(type);
            os.writeInt(size);
            
            int maxBuffSize = 1024;
            int writeBuffSize = data.remaining() < maxBuffSize ? data.remaining() : maxBuffSize;
            byte[] tmp = new byte[writeBuffSize]; 
            while ( data.remaining() > 0 )
            {
                int length = data.remaining() > tmp.length ? tmp.length : data.remaining();
                data.get( tmp, 0, length);
                os.write(tmp, 0, length);
            }
            
            if ( padding > 0 )
            {
                os.write( new byte[padding] );
            }
        }
    }
    
    
    
}
