
package NR;

import java.text.DecimalFormat;
import groovySci.FFT.FFTCommon;
import static groovySci.FFT.FFTCommon.swap;

public class NRFFT {
 
// print all  the FFT results
public static void printft(double []data) {
    DecimalFormat fiveDigit = new DecimalFormat("0.00000E0");
    int L = data.length;
    int Ld2 = L/2;  // half the data length, since each frequency occupies two slots
    
    System.out.println("\n\nFreq       Real Part                Imaginary Part\n");
    //  positive frequencies are described in data[0..Ld2-1] in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    int magnitude = 0;
    for (int k=0; k<Ld2; k+=2)  
        System.out.println(""+ (magnitude++) +"               "+ fiveDigit.format(data[k])+"            "+fiveDigit.format(data[k+1]));

    // negative frequencies are described in data[L-1..Ld2]  in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    magnitude = 1;
    for (int k=L-1; k>Ld2; k-=2)
        System.out.println(""+(-magnitude++) +"              "+ fiveDigit.format(data[k-1])+"            "+fiveDigit.format(data[k]));
    }

// print from  the FFT results the first NFreqs
public static void printft(double []data, int nfreqs) {
    DecimalFormat fiveDigit = new DecimalFormat("0.00000E0");
    int L = data.length;
    int Ld2 = L/2;  // half the data length, since each frequency occupies two slots
    
    System.out.println("\n\nFreq       Real Part                Imaginary Part\n");
    //  positive frequencies are described in data[0..Ld2-1] in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    int magnitude = 0;
    for (int k=0; k<Ld2; k+=2)   {
        System.out.println(""+ (magnitude++) +"               "+ fiveDigit.format(data[k])+"            "+fiveDigit.format(data[k+1]));
        if (magnitude==nfreqs)  break;
    }
    // negative frequencies are described in data[L-1..Ld2]  in increasing magnitudes
    //  and with two slots for each frequency (for the real and imaginary part of each coefficient)
    magnitude = 1;
    for (int k=L-1; k>Ld2; k-=2)  {
        System.out.println(""+(-magnitude++) +"              "+ fiveDigit.format(data[k-1])+"            "+fiveDigit.format(data[k]));
          if (magnitude==nfreqs)  break;
       }
    }


// a simplified interface to four1 
    // does not destroy data, and outputs FFT in realfft and imfft arrays
public static void  four1S(double [] data, double [] realffts, double [] imffts) {
    double [] cpdata = Common.copy(data);  // copy the input data
    four1(cpdata, 1);  // perform the FFT
    int N = cpdata.length;
    int cnt=0;
    for (int k=0; k<N; k+=2) {
        realffts[cnt] = cpdata[k];
        imffts[cnt] = cpdata[k+1];
        cnt++;
    }
    
}
 

public static void four1(double [] data, int  isign) {
    int  nn,mmax,m,j,istep,i;
    nn = (data.length/2);
    int n = nn << 1;
    double  wtemp,wr,wpr,wpi,wi,theta,tempr,tempi;
    if (n<2 || ( n & (n-1)) !=0) {
            System.out.println("n must be power of 2 in four1");
            return;
        }
    j = 1;
    for (i=1;i<n;i+=2) {
        if (j > i) {
            swap (data, j-1, i-1);
            swap(data, j, i);
        }
    m=nn;
    while (m >= 2 && j > m) {
        j -= m;
        m >>= 1;
        }
    j += m;
    }
    mmax=2;
    while (n > mmax) {
        istep=mmax << 1;
        theta=isign*(6.28318530717959/mmax);
        wtemp=Math.sin(0.5*theta);
        wpr = -2.0*wtemp*wtemp;
        wpi=Math.sin(theta);
        wr=1.0;
        wi=0.0;
        for (m=1;m<mmax;m+=2) {
            for (i=m;i<=n;i+=istep) {
	j=i+mmax;
	tempr=wr*data[j-1]-wi*data[j];
	tempi=wr*data[j]+wi*data[j-1];
	data[j-1]=data[i-1]-tempr;
	data[j]=data[i]-tempi;
	data[i-1] += tempr;
	data[i] += tempi;
	}
	wr=(wtemp=wr)*wpr-wi*wpi+wr;
	wi=wi*wpr+wtemp*wpi+wi;
	}
    mmax=istep;
	}
}


public static void realft(double [] data, int  isign) {
	int i, i1, i2,i3,i4;
                  int n=data.length;
	double  c1=0.5,c2,h1r,h1i,h2r,h2i,wr,wi,wpr,wpi,wtemp;
	double  theta=3.141592653589793238/ (double) (n>>1);
	if (isign == 1) {
		c2 = -0.5;
		four1(data, 1);
	} else {
		c2 = 0.5;
		theta = -theta;
	}
	wtemp = Math.sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi = Math.sin(theta);
	wr = 1.0+wpr;
	wi = wpi;
	for (i=1;i<(n>>2);i++) {
		i2=1+(i1=i+i);
		i4=1+(i3=n-i1);
		h1r=c1*(data[i1]+data[i3]);
		h1i=c1*(data[i2]-data[i4]);
		h2r= -c2*(data[i2]+data[i4]);
		h2i=c2*(data[i1]-data[i3]);
		data[i1]=h1r+wr*h2r-wi*h2i;
		data[i2]=h1i+wr*h2i+wi*h2r;
		data[i3]=h1r-wr*h2r+wi*h2i;
		data[i4]= -h1i+wr*h2i+wi*h2r;
		wr=(wtemp=wr)*wpr-wi*wpi+wr;
		wi=wi*wpr+wtemp*wpi+wi;
	}
	if (isign == 1) {
		data[0] = (h1r=data[0])+data[1];
		data[1] = h1r-data[1];
	} else {
		data[0]=c1*((h1r=data[0])+data[1]);
		data[1]=c1*(h1r-data[1]);
		four1(data,  -1);
	}
}

public static void sinft(double []  y) {
    
	int  j, n = y.length;
	double  sum,y1,y2,theta,wi=0.0,wr=1.0,wpi,wpr,wtemp;
	theta =3.141592653589793238 / (double)n;
	wtemp = Math.sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi = Math.sin(theta);
	y[0]=0.0;
	for (j=1;j<(n>>1)+1;j++) {
		wr=(wtemp=wr)*wpr-wi*wpi+wr;
		wi=wi*wpr+wtemp*wpi+wi;
		y1=wi*(y[j]+y[n-j]);
		y2=0.5*(y[j]-y[n-j]);
		y[j]=y1+y2;
		y[n-j]=y1-y2;
	}
	realft(y,1);
	y[0]*=0.5;
	sum=y[1]=0.0;
	for (j=0;j<n-1;j+=2) {
		sum += y[j];
		y[j]=y[j+1];
		y[j+1]=sum;
	}
}

public static void  cosft1(double [] y) {
	final double  PI=3.141592653589793238;
	int  j, n = y.length-1;
	double  sum,y1,y2,theta,wi=0.0,wpi,wpr,wr=1.0,wtemp;
	double [] yy = new double[n];
	theta=PI/n;
	wtemp=Math.sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi = Math.sin(theta);
	sum=0.5*(y[0]-y[n]);
	yy[0]=0.5*(y[0]+y[n]);
	for (j=1;j<n/2;j++) {
                    wr=(wtemp=wr)*wpr-wi*wpi+wr;
                    wi=wi*wpr+wtemp*wpi+wi;
                    y1=0.5*(y[j]+y[n-j]);
                    y2=(y[j]-y[n-j]);
                    yy[j]=y1-wi*y2;
                    yy[n-j]=y1+wi*y2;
                    sum += wr*y2;
	}
	yy[n/2]=y[n/2];
	realft(yy,1);
	for (j=0;j<n;j++) y[j]=yy[j];
	y[n]=y[1];
	y[1]=sum;
	for (j=3;j<n;j+=2) {
		sum += y[j];
		y[j]=sum;
	}
}

public static void cosft2(double [] y, int  isign) {
    final double PI=3.141592653589793238;
    int   i, n = y.length;
    double  sum,sum1,y1,y2,ytemp,theta,wi=0.0,wi1,wpi,wpr,wr=1.0,wr1,wtemp;
    theta = 0.5*PI/n;
    wr1 = Math.cos(theta);
    wi1 = Math.sin(theta);
    wpr = -2.0*wi1*wi1;
    wpi = Math.sin(2.0*theta);
    if (isign == 1) {
        for (i=0;i<n/2;i++) {
	y1=0.5*(y[i]+y[n-1-i]);
	y2=wi1*(y[i]-y[n-1-i]);
	y[i]=y1+y2;
	y[n-1-i]=y1-y2;
	wr1=(wtemp=wr1)*wpr-wi1*wpi+wr1;
	wi1=wi1*wpr+wtemp*wpi+wi1;
        }
    realft(y,1);
    for (i=2;i<n;i+=2) {
	wr=(wtemp=wr)*wpr-wi*wpi+wr;
	wi=wi*wpr+wtemp*wpi+wi;
	y1=y[i]*wr-y[i+1]*wi;
	y2=y[i+1]*wr+y[i]*wi;
	y[i]=y1;
	y[i+1]=y2;
            }
	sum=0.5*y[1];
	for (i=n-1;i>0;i-=2) {
                    sum1=sum;
                    sum += y[i];
                    y[i]=sum1;
	}
	} else if (isign == -1) {
                        ytemp=y[n-1];
                        for (i=n-1;i>2;i-=2)
                            y[i]=y[i-2]-y[i];
                        y[1]=2.0*ytemp;
                        for (i=2;i<n;i+=2) {
		wr=(wtemp=wr)*wpr-wi*wpi+wr;
		wi=wi*wpr+wtemp*wpi+wi;
		y1=y[i]*wr+y[i+1]*wi;
		y2=y[i+1]*wr-y[i]*wi;
		y[i]=y1;
		y[i+1]=y2;
		}
                    realft(y,-1);
                    for (i=0;i<n/2;i++) {
                        y1=y[i]+y[n-1-i];
                        y2=(0.5/wi1)*(y[i]-y[n-1-i]);
                        y[i]=0.5*(y1+y2);
                        y[n-1-i]=0.5*(y1-y2);
                        wr1=(wtemp=wr1)*wpr-wi1*wpi+wr1;
                        wi1=wi1*wpr+wtemp*wpi+wi1;
	}
        }
}



void  fourn(double [] data,  int [] nn,   int isign)
{
    int idim, i1, i2, i3, i2rev, i3rev, ip1, ip2, ip3, ifp1, ifp2;
    int ibit, k1, k2, n, nprev, nrem, ntot;
    double   tempi, tempr, theta, wi, wpi, wpr, wr, wtemp;

    int ndim = nn.length;
    ntot = data.length/2;
    nprev=1;
    for (idim=ndim-1;idim>=0;idim--) {
        n=nn[idim];
        nrem=ntot/(n*nprev);
        ip1=nprev << 1;
        ip2=ip1*n;
        ip3=ip2*nrem;
        i2rev=0;
        for (i2=0;i2<ip2;i2+=ip1) {
            if (i2 < i2rev) {
	for (i1=i2;i1<i2+ip1-1;i1+=2) {
                        for (i3=i1;i3<ip3;i3+=ip2) {
                            i3rev=i2rev+i3-i2;
                            swap(data, i3, i3rev);
                            swap(data, i3+1, i3rev+1);
            }
        }
        }
    ibit=ip2 >> 1;
    while (ibit >= ip1 && i2rev+1 > ibit) {
        i2rev -= ibit;
        ibit >>= 1;
        }
        i2rev += ibit;
    }
    ifp1=ip1;
    while (ifp1 < ip2) {
        ifp2=ifp1 << 1;
        theta=isign*6.28318530717959/(ifp2/ip1);
        wtemp=Math.sin(0.5*theta);
        wpr= -2.0*wtemp*wtemp;
        wpi=Math.sin(theta);
        wr=1.0;
        wi=0.0;
        for (i3=0;i3<ifp1;i3+=ip1) {
                for (i1=i3;i1<i3+ip1-1;i1+=2) {
                	for (i2=i1;i2<ip3;i2+=ifp2) {
                        k1=i2;
                        k2=k1+ifp1;
                        tempr=wr*data[k2]-wi*data[k2+1];
                        tempi=wr*data[k2+1]+wi*data[k2];
                        data[k2]=data[k1]-tempr;
                        data[k2+1]=data[k1+1]-tempi;
                        data[k1] += tempr;
                        data[k1+1] += tempi;
	}
            }
	wr=(wtemp=wr)*wpr-wi*wpi+wr;
	wi=wi*wpr+wtemp*wpi+wi;
	}
	ifp1=ifp2;
	}
	nprev *= n;
	}
}

void  rlft3(double [][][] data, double [][] speq,  int isign)
{
    int i1, i2, i3, j1, j2, j3, ii3, k1, k2, k3, k4;
    double  theta, wi, wpi, wpr, wr, wtemp;
    double  c1, c2, h1r, h1i, h2r, h2i;
    int   [] nn = new int[3];

    int  nn1 = data.length;
    int  nn2=data[0].length;
    int  nn3=data[0][0].length;
    c1=0.5;
    c2= -0.5*isign;
    theta=isign*(6.28318530717959/nn3);
    wtemp=Math.sin(0.5*theta);
    wpr= -2.0*wtemp*wtemp;
    wpi=Math.sin(theta);    
    nn[0]=nn1;
    nn[1]=nn2;
    nn[2]=nn3 >> 1;
    double []  data_v = FFTCommon.threeDMatrixToVector( data);
    if (isign == 1) {
        fourn(data_v, nn, isign);
        k1=0;
        for (i1=0;i1<nn1;i1++)
            for (i2=0,j2=0;i2<nn2;i2++,k1+=nn3) {
	speq[i1][j2++]=data_v[k1];
	speq[i1][j2++]=data_v[k1+1];
	}
    }
for (i1=0;i1<nn1;i1++) {
    j1=(i1 != 0 ? nn1-i1 : 0);
    
    wr=1.0;
    wi=0.0;
    for (ii3=0;ii3<=(nn3>>1);ii3+=2) {
        k1=i1*nn2*nn3;
        k3=j1*nn2*nn3;
        for (i2=0;i2<nn2;i2++,k1+=nn3) {
            if (ii3 == 0) {
	j2=(i2 != 0 ? ((nn2-i2)<<1) : 0);
	h1r=c1*(data_v[k1]+speq[j1][j2]);
	h1i=c1*(data_v[k1+1]-speq[j1][j2+1]);
	h2i=c2*(data_v[k1]-speq[j1][j2]);
	h2r= -c2*(data_v[k1+1]+speq[j1][j2+1]);
	data_v[k1]=h1r+h2r;
	data_v[k1+1]=h1i+h2i;
	speq[j1][j2]=h1r-h2r;
	speq[j1][j2+1]=h2i-h1i;
                } else {
	j2=(i2 != 0 ? nn2-i2 : 0);
	j3=nn3-ii3;
	k2=k1+ii3;
	k4=k3+j2*nn3+j3;
	h1r=c1*(data_v[k2]+data_v[k4]);
	h1i=c1*(data_v[k2+1]-data_v[k4+1]);
	h2i=c2*(data_v[k2]-data_v[k4]);
	h2r= -c2*(data_v[k2+1]+data_v[k4+1]);
	data_v[k2]=h1r+wr*h2r-wi*h2i;
	data_v[k2+1]=h1i+wr*h2i+wi*h2r;
	data_v[k4]=h1r-wr*h2r+wi*h2i;
	data_v[k4+1]= -h1i+wr*h2i+wi*h2r;
	}
        }
        wr=(wtemp=wr)*wpr-wi*wpi+wr;
        wi=wi*wpr+wtemp*wpi+wi;
        }
    }
    if (isign == -1) fourn(data_v,nn,isign);
    k1=0;
    for (i1=0;i1<nn1;i1++)
        for (i2=0;i2<nn2;i2++)
            for (i3=0;i3<nn3;i3++) data[i1][i2][i3]=data_v[k1++];
 }



void  convlv( double [] data, int [] respns,  int isign, double [] ans)
{
    int  i, no2;
    double   mag2, tmp;

    int n = data.length;
    int m = respns.length;
    double []  temp = new double [n];
    temp[0] = respns[0];
    for (i=1; i<(m+1)/2; i++) {
        temp[i]=respns[i];
        temp[n-i]=respns[m-i];
    }
    for (i=(m+1)/2;i<n-(m-1)/2;i++)
        temp[i]=0.0;
    for (i=0;i<n;i++)
        ans[i]=data[i];
    realft(ans,1);
    realft(temp,1);
    no2=n>>1;
    if (isign == 1) {
        for (i=2;i<n;i+=2) {
            tmp=ans[i];
            ans[i]=(ans[i]*temp[i]-ans[i+1]*temp[i+1])/no2;
            ans[i+1]=(ans[i+1]*temp[i]+tmp*temp[i+1])/no2;
        }
    ans[0]=ans[0]*temp[0]/no2;
    ans[1]=ans[1]*temp[1]/no2;
    } else if (isign == -1) {
    for (i=2;i<n;i+=2) {
	if ((mag2=(temp[i]*temp[i])+(temp[i+1]*temp[i+1])) == 0.0) {
  System.out.println("Deconvolving at response zero in convlv");
  return;
        }
    tmp=ans[i];
    ans[i]=(ans[i]*temp[i]+ans[i+1]*temp[i+1])/mag2/no2;
    ans[i+1]=(ans[i+1]*temp[i]-tmp*temp[i+1])/mag2/no2;
    }
    if (temp[0] == 0.0 || temp[1] == 0.0)
        {
  System.out.println("Deconvolving at response zero in convlv");
  return;
        }
 ans[0]=ans[0]/temp[0]/no2;
 ans[1]=ans[1]/temp[1]/no2;
	} 
    else 
        {
  System.out.println("No meaning for isign in convlv");
  return;
        }
 realft(ans,-1);
}



void correl(double [] data1, int [] data2,  double [] ans)
{
    int no2, i;
    double   tmp;

    int n=data1.length;
    double []   temp = new double [n];
    for (i=0;i<n;i++) {
        ans[i]=data1[i];
        temp[i]=data2[i];
    }
    realft(ans,1);
    realft(temp,1);
    no2=n>>1;
    for (i=2;i<n;i+=2) {
        tmp=ans[i];
        ans[i]=(ans[i]*temp[i]+ans[i+1]*temp[i+1])/no2;
        ans[i+1]=(ans[i+1]*temp[i]-tmp*temp[i+1])/no2;
    }
    ans[0]=ans[0]*temp[0]/no2;
    ans[1]=ans[1]*temp[1]/no2;
    realft(ans,-1);
}


double  window(int j, double   a, double  b)
  {
 return 1.0-Math.abs((j-a)*b);     // Bartlett
		// return 1.0;                // Square
		// return 1.0-SQR((j-a)*b);   // Welch
	}


void  spctrm( double [] fp, double [] p, int k, boolean ovrlap)
{
    int mm, m4, kk, joffn, joff, j2, j;
    double  w, facp, facm, sumw = 0.0, den = 0.0;

    int dataCnt = 0;
    int totalDataLength = fp.length;
    int m=p.length;
    
    mm=m << 1;
    m4=mm << 1;
    double []  w1 = new double[m4];
    double [] w2 = new double [m];
    facm=m;
    facp=1.0/m;
    for (j=0;j<mm;j++) {
        double wdv = window(j,facm,facp);
        sumw += wdv*wdv; 
        for (j=0;j<m;j++) p[j]=0.0;
            if (ovrlap)
	for (j=0;j<m;j++)
             w2[j] = fp[dataCnt++];
             for (kk=0;kk<k;kk++) {
	for (joff=0;joff<2;joff++) {
                        if (ovrlap) {
                            for (j=0;j<m;j++) w1[joff+j+j]=w2[j];
                                for (j=0;j<m;j++)
                                        w2[j] = fp[dataCnt++];
                                        joffn=joff+mm-1;
                                        for (j=0;j<m;j++) w1[joffn+j+j+1]=w2[j];
		}  // ovrlap
                            else {
                                for (j=joff;j<m4;j+=2)
                                        w1[j] = fp[dataCnt++];
                        }
	}
	for (j=0;j<mm;j++) {
                        j2=j+j;
                        w=window(j,facm,facp);
                        w1[j2+1] *= w;
                        w1[j2] *= w;
	}
	four1(w1, 1);
	p[0] += (w1[0]*w1[0])+(w1[1]*w1[1]);
	for (j=1;j<m;j++) {
                        j2=j+j;
                        p[j] += (w1[j2+1]*w1[j2+1]+w1[j2]*w1[j2])+(w1[m4-j2+1]*w1[m4-j2+1])+(w1[m4-j2]*w1[m4-j2]);
		}
	den += sumw;
	}
	den *= m4;
	for (j=0;j<m;j++) p[j] /= den;

  }
}


// Given a real vector of data[0..n-1], this routine routine returns m linear prediction coefficients as
// d[0..m-1], and returns the mean square discrepancy as xms     
void  memcof(double []data,  double [] xms,  double [] d)
{
    int  k, j, i;
    double  p=0.0;

    int n = data.length;
    int m = d.length;
    double []  wk1 = new double[n];
    double [] wk2 = new double[n];
    double [] wkm = new double[m];
    
    for (j=0;j<n;j++) p += Common.SQR(data[j]);
        xms[0] = p/n;
        wk1[0] = data[0];
        wk2[n-2] = data[n-1];
        for (j=1;j<n-1;j++) {
            wk1[j]=data[j];
            wk2[j-1]=data[j];
            }
        for (k=0;k<m;k++) {
        double  num=0.0,denom=0.0;
        for (j=0;j<(n-k-1);j++) {
            num += (wk1[j]*wk2[j]);
            denom += (Common.SQR(wk1[j])+Common.SQR(wk2[j]));
		}
    d[k]=2.0*num/denom;
    xms[0] *= (1.0-Common.SQR(d[k]));
    for (i=0;i<k;i++)
        d[i]=wkm[i]-d[k]*wkm[k-1-i];
        if (k == m-1)
            return;
    for (i=0;i<=k;i++) wkm[i]=d[i];
        for (j=0;j<(n-k-2);j++) {
            wk1[j] -= (wkm[k]*wk2[j]);
            wk2[j]=wk2[j+1]-wkm[k]*wk1[j+1];
	}
        }
	System.out.println("never get here in memcof.");
}



// Given the LP coefficients d[0..m-1], this routine finds all roots of the characteristric polynomial,
// reflects any roots that are outside the unit circle back inside, and then returns a modified set of coefficients d[0..m-1]
/*void  fixrts(double [] d)
{
    boolean  polish = true;
    int i,j;

    int m=d.length;
	Vec_CPLX_DP a(m+1),roots(m);
	a[m]=1.0;
	for (j=0;j<m;j++)
		a[j]= -d[m-1-j];
	zroots(a,roots,polish);
	for (j=0;j<m;j++)
		if (abs(roots[j]) > 1.0)
			roots[j]=1.0/conj(roots[j]);
	a[0]= -roots[0];
	a[1]=1.0;
	for (j=1;j<m;j++) {
		a[j+1]=1.0;
		for (i=j;i>=1;i--)
			a[i]=a[i-1]-roots[j]*a[i];
		a[0]= -roots[j]*a[0];
	}
	for (j=0;j<m;j++)
		d[m-1-j] = -real(a[j]);
}
*/

}
