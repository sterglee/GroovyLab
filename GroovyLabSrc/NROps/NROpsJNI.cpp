

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include <memory>
#include <iostream>

#include "NROps_NROps.h"


#include "nr3.h" 
  /**
   * Replaces data[0..2*n-1] by its discrete Fourier transform, if isign is
   * input as 1; or replaces data[0..2*n-1] by n times its inverse discrete
   * Fourier transform, if isign is input as -1. data is a complex array of
   * length n stored as a real array of length 2*n. n must be an integer power
   * of 2.
   * 
   * @param data
   * @param n
   * @param isign
   */


void four1(Doub *data, const Int n, const Int isign) {
	Int nn,mmax,m,j,istep,i;
	Doub wtemp,wr,wpr,wpi,wi,theta,tempr,tempi;
	if (n<2 || n&(n-1)) throw("n must be power of 2 in four1");
	nn = n << 1;
	j = 1;
	for (i=1;i<nn;i+=2) {
		if (j > i) {
			SWAP(data[j-1],data[i-1]);
			SWAP(data[j],data[i]);
		}
		m=n;
		while (m >= 2 && j > m) {
			j -= m;
			m >>= 1;
		}
		j += m;
	}
	mmax=2;
	while (nn > mmax) {
		istep=mmax << 1;
		theta=isign*(6.28318530717959/mmax);
		wtemp=sin(0.5*theta);
		wpr = -2.0*wtemp*wtemp;
		wpi=sin(theta);
		wr=1.0;
		wi=0.0;
		for (m=1;m<mmax;m+=2) {
			for (i=m;i<=nn;i+=istep) {
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

void four1(VecDoub_IO &data, const Int isign) {
	four1(&data[0],data.size()/2,isign);
}

void four1(VecComplex_IO &data, const Int isign) {
	four1((Doub*)(&data[0]),data.size(),isign);
}
struct WrapVecDoub {
	VecDoub vvec;
	VecDoub &v;
	Int n, mask;

	WrapVecDoub(const Int nn) : vvec(nn), v(vvec), n(nn/2),
	mask(n-1) {validate();}

	WrapVecDoub(VecDoub &vec) : v(vec), n(vec.size()/2),
	mask(n-1) {validate();}
		
	void validate() {if (n&(n-1)) throw("vec size must be power of 2");}

	inline Complex& operator[] (Int i) {return (Complex &)v[(i&mask) << 1];}

	inline Doub& real(Int i) {return v[(i&mask) << 1];}

	inline Doub& imag(Int i) {return v[((i&mask) << 1)+1];}

	operator VecDoub&() {return v;}

};
 /**
   * Calculates the Fourier transform of a set of n real-valued data points.
   * Replaces these data (which are stored in array data[0..n-1]) by the
   * positive frequency half of their complex Fourier transform. The real-valued
   * first and last components of the complex transform are returned as elements
   * data[0] and data[1], respectively. n must be a power of 2. This routine
   * also calculates the inverse transform of a complex data array if it is the
   * transform of real data. (Result in this case must be multiplied by 2/n.)
   * 
   * @param data
   * @param isign
   */
void realft(VecDoub_IO &data, const Int isign) {
	Int i,i1,i2,i3,i4,n=data.size();
	Doub c1=0.5,c2,h1r,h1i,h2r,h2i,wr,wi,wpr,wpi,wtemp;
	Doub theta=3.141592653589793238/Doub(n>>1);
	if (isign == 1) {
		c2 = -0.5;
		four1(data,1);
	} else {
		c2=0.5;
		theta = -theta;
	}
	wtemp=sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi=sin(theta);
	wr=1.0+wpr;
	wi=wpi;
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
		four1(data,-1);
	}
}

 /**
   * Calculates the Fourier transform of a set of n real-valued data points.
   * Replaces these data (which are stored in array data[0..n-1]) by the
   * positive frequency half of their complex Fourier transform. The real-valued
   * first and last components of the complex transform are returned as elements
   * data[0] and data[1], respectively. n must be a power of 2. This routine
   * also calculates the inverse transform of a complex data array if it is the
   * transform of real data. (Result in this case must be multiplied by 2/n.)
   * 
   * @param data
   * @param isign
   */
void realft(double *data, int n,  const Int isign) {
	Int i,i1,i2,i3,i4;
	Doub c1=0.5,c2,h1r,h1i,h2r,h2i,wr,wi,wpr,wpi,wtemp;
	Doub theta=3.141592653589793238/Doub(n>>1);
	if (isign == 1) {
		c2 = -0.5;
		four1(data,n/2, 1);
	} else {
		c2=0.5;
		theta = -theta;
	}
	wtemp=sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi=sin(theta);
	wr=1.0+wpr;
	wi=wpi;
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
		four1(data,n/2, -1);
	}
}

void sinft(VecDoub_IO &y) {
	Int j,n=y.size();
	Doub sum,y1,y2,theta,wi=0.0,wr=1.0,wpi,wpr,wtemp;
	theta=3.141592653589793238/Doub(n);
	wtemp=sin(0.5*theta);
	wpr= -2.0*wtemp*wtemp;
	wpi=sin(theta);
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
void cosft1(VecDoub_IO &y) {
	const Doub PI=3.141592653589793238;
	Int j,n=y.size()-1;
	Doub sum,y1,y2,theta,wi=0.0,wpi,wpr,wr=1.0,wtemp;
	VecDoub yy(n);
	theta=PI/n;
	wtemp=sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi=sin(theta);
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
void cosft2(VecDoub_IO &y, const Int isign) {
	const Doub PI=3.141592653589793238;
	Int i,n=y.size();
	Doub sum,sum1,y1,y2,ytemp,theta,wi=0.0,wi1,wpi,wpr,wr=1.0,wr1,wtemp;
	theta=0.5*PI/n;
	wr1=cos(theta);
	wi1=sin(theta);
	wpr = -2.0*wi1*wi1;
	wpi=sin(2.0*theta);
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

void fourn(double *data, int *nn, int ndim,  int  isign) {
	Int idim,i1,i2,i3,i2rev,i3rev,ip1,ip2,ip3,ifp1,ifp2;
	Int ibit,k1,k2,n,nprev,nrem,ntot=1;
	Doub tempi,tempr,theta,wi,wpi,wpr,wr,wtemp;
	for (idim=0;idim<ndim;idim++) ntot *= nn[idim];
	if (ntot<2 || ntot&(ntot-1)) throw("must have powers of 2 in fourn");
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
						SWAP(data[i3],data[i3rev]);
						SWAP(data[i3+1],data[i3rev+1]);
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
			wtemp=sin(0.5*theta);
			wpr= -2.0*wtemp*wtemp;
			wpi=sin(theta);
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


void fourn(Doub *data, VecInt_I &nn, const Int isign) {
	Int idim,i1,i2,i3,i2rev,i3rev,ip1,ip2,ip3,ifp1,ifp2;
	Int ibit,k1,k2,n,nprev,nrem,ntot=1,ndim=nn.size();
	Doub tempi,tempr,theta,wi,wpi,wpr,wr,wtemp;
	for (idim=0;idim<ndim;idim++) ntot *= nn[idim];
	if (ntot<2 || ntot&(ntot-1)) throw("must have powers of 2 in fourn");
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
						SWAP(data[i3],data[i3rev]);
						SWAP(data[i3+1],data[i3rev+1]);
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
			wtemp=sin(0.5*theta);
			wpr= -2.0*wtemp*wtemp;
			wpi=sin(theta);
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

void fourn(VecDoub_IO &data, VecInt_I &nn, const Int isign) {
	fourn(&data[0],nn,isign);
}
void rlft3(Doub *data, Doub *speq, const Int isign,
	const Int nn1, const Int nn2, const Int nn3) {
	Int i1,i2,i3,j1,j2,j3,k1,k2,k3,k4;
	Doub theta,wi,wpi,wpr,wr,wtemp;
	Doub c1,c2,h1r,h1i,h2r,h2i;
	VecInt nn(3);
	VecDoubp spq(nn1);
	for (i1=0;i1<nn1;i1++) spq[i1] = speq + 2*nn2*i1;
	c1 = 0.5;
	c2 = -0.5*isign;
	theta = isign*(6.28318530717959/nn3);
	wtemp = sin(0.5*theta);
	wpr = -2.0*wtemp*wtemp;
	wpi = sin(theta);
	nn[0] = nn1;
	nn[1] = nn2;
	nn[2] = nn3 >> 1;
	if (isign == 1) {
		fourn(data,nn,isign);
		k1=0;
		for (i1=0;i1<nn1;i1++)
			for (i2=0,j2=0;i2<nn2;i2++,k1+=nn3) {
				spq[i1][j2++]=data[k1];
				spq[i1][j2++]=data[k1+1];
			}
	}
	for (i1=0;i1<nn1;i1++) {
		j1=(i1 != 0 ? nn1-i1 : 0);
		wr=1.0;
		wi=0.0;
		for (i3=0;i3<=(nn3>>1);i3+=2) {
			k1=i1*nn2*nn3;
			k3=j1*nn2*nn3;
			for (i2=0;i2<nn2;i2++,k1+=nn3) {
				if (i3 == 0) {
					j2=(i2 != 0 ? ((nn2-i2)<<1) : 0);
					h1r=c1*(data[k1]+spq[j1][j2]);
					h1i=c1*(data[k1+1]-spq[j1][j2+1]);
					h2i=c2*(data[k1]-spq[j1][j2]);
					h2r= -c2*(data[k1+1]+spq[j1][j2+1]);
					data[k1]=h1r+h2r;
					data[k1+1]=h1i+h2i;
					spq[j1][j2]=h1r-h2r;
					spq[j1][j2+1]=h2i-h1i;
				} else {
					j2=(i2 != 0 ? nn2-i2 : 0);
					j3=nn3-i3;
					k2=k1+i3;
					k4=k3+j2*nn3+j3;
					h1r=c1*(data[k2]+data[k4]);
					h1i=c1*(data[k2+1]-data[k4+1]);
					h2i=c2*(data[k2]-data[k4]);
					h2r= -c2*(data[k2+1]+data[k4+1]);
					data[k2]=h1r+wr*h2r-wi*h2i;
					data[k2+1]=h1i+wr*h2i+wi*h2r;
					data[k4]=h1r-wr*h2r+wi*h2i;
					data[k4+1]= -h1i+wr*h2i+wi*h2r;
				}
			}
			wr=(wtemp=wr)*wpr-wi*wpi+wr;
			wi=wi*wpr+wtemp*wpi+wi;
		}
	}
	if (isign == -1) fourn(data,nn,isign);
}

void rlft3(Mat3DDoub_IO &data, MatDoub_IO &speq, const Int isign) {
	if (speq.nrows() != data.dim1() || speq.ncols() != 2*data.dim2())
		throw("bad dims in rlft3");
	rlft3(&data[0][0][0],&speq[0][0],isign,data.dim1(),data.dim2(),data.dim3());
}

void rlft3(MatDoub_IO &data, VecDoub_IO &speq, const Int isign) {
	if (speq.size() != 2*data.nrows()) throw("bad dims in rlft3");
	rlft3(&data[0][0],&speq[0],isign,1,data.nrows(),data.ncols());
}


struct Unsymmeig {
	Int n;
	MatDoub a,zz;
	VecComplex wri;
	VecDoub scale;
	VecInt perm;
	Bool yesvecs,hessen;

	Unsymmeig(MatDoub_I &aa, Bool yesvec=true, Bool hessenb=false) :
		n(aa.nrows()), a(aa), zz(n,n,0.0), wri(n), scale(n,1.0), perm(n),
		yesvecs(yesvec), hessen(hessenb)
	{
		balance();
		if (!hessen) elmhes();
		if (yesvecs) {
			for (Int i=0;i<n;i++)
				zz[i][i]=1.0;
			if (!hessen) eltran();
			hqr2();
			balbak();
			sortvecs();
		} else {
			hqr();
			sort();
		}
	}
	void balance();
	void elmhes();
	void eltran();
	void hqr();
	void hqr2();
	void balbak();
	void sort();
	void sortvecs();
};
void Unsymmeig::balance()
{
	const Doub RADIX = numeric_limits<Doub>::radix;
	Bool done=false;
	Doub sqrdx=RADIX*RADIX;
	while (!done) {
		done=true;
		for (Int i=0;i<n;i++) {
			Doub r=0.0,c=0.0;
			for (Int j=0;j<n;j++)
				if (j != i) {
					c += abs(a[j][i]);
					r += abs(a[i][j]);
				}
			if (c != 0.0 && r != 0.0) {
				Doub g=r/RADIX;
				Doub f=1.0;
				Doub s=c+r;
				while (c<g) {
					f *= RADIX;
					c *= sqrdx;
				}
				g=r*RADIX;
				while (c>g) {
					f /= RADIX;
					c /= sqrdx;
				}
				if ((c+r)/f < 0.95*s) {
					done=false;
					g=1.0/f;
					scale[i] *= f;
					for (Int j=0;j<n;j++) a[i][j] *= g;
					for (Int j=0;j<n;j++) a[j][i] *= f;
				}
			}
		}
	}
}
void Unsymmeig::balbak()
{
	for (Int i=0;i<n;i++)
		for (Int j=0;j<n;j++)
			zz[i][j] *= scale[i];
}
void Unsymmeig::elmhes()
{
	for (Int m=1;m<n-1;m++) {
		Doub x=0.0;
		Int i=m;
		for (Int j=m;j<n;j++) {
			if (abs(a[j][m-1]) > abs(x)) {
				x=a[j][m-1];
				i=j;
			}
		}
		perm[m]=i;
		if (i != m) {
			for (Int j=m-1;j<n;j++) SWAP(a[i][j],a[m][j]);
			for (Int j=0;j<n;j++) SWAP(a[j][i],a[j][m]);
		}
		if (x != 0.0) {
			for (i=m+1;i<n;i++) {
				Doub y=a[i][m-1];
				if (y != 0.0) {
					y /= x;
					a[i][m-1]=y;
					for (Int j=m;j<n;j++) a[i][j] -= y*a[m][j];
					for (Int j=0;j<n;j++) a[j][m] += y*a[j][i];
				}
			}
		}
	}
}
void Unsymmeig::eltran()
{
	for (Int mp=n-2;mp>0;mp--) {
		for (Int k=mp+1;k<n;k++)
			zz[k][mp]=a[k][mp-1];
		Int i=perm[mp];
		if (i != mp) {
			for (Int j=mp;j<n;j++) {
				zz[mp][j]=zz[i][j];
				zz[i][j]=0.0;
			}
			zz[i][mp]=1.0;
		}
	}
}
void Unsymmeig::hqr()
{
	Int nn,m,l,k,j,its,i,mmin;
	Doub z,y,x,w,v,u,t,s,r,q,p,anorm=0.0;

	const Doub EPS=numeric_limits<Doub>::epsilon();
	for (i=0;i<n;i++)
		for (j=MAX(i-1,0);j<n;j++)
			anorm += abs(a[i][j]);
	nn=n-1;
	t=0.0;
	while (nn >= 0) {
		its=0;
		do {
			for (l=nn;l>0;l--) {
				s=abs(a[l-1][l-1])+abs(a[l][l]);
				if (s == 0.0) s=anorm;
				if (abs(a[l][l-1]) <= EPS*s) {
					a[l][l-1] = 0.0;
					break;
				}
			}
			x=a[nn][nn];
			if (l == nn) {
				wri[nn--]=x+t;
			} else {
				y=a[nn-1][nn-1];
				w=a[nn][nn-1]*a[nn-1][nn];
				if (l == nn-1) {
					p=0.5*(y-x);
					q=p*p+w;
					z=sqrt(abs(q));
					x += t;
					if (q >= 0.0) {
						z=p+SIGN(z,p);
						wri[nn-1]=wri[nn]=x+z;
						if (z != 0.0) wri[nn]=x-w/z;
					} else {
						wri[nn]=Complex(x+p,-z);
						wri[nn-1]=conj(wri[nn]);
					}
					nn -= 2;
				} else {
					if (its == 30) throw("Too many iterations in hqr");
					if (its == 10 || its == 20) {
						t += x;
						for (i=0;i<nn+1;i++) a[i][i] -= x;
						s=abs(a[nn][nn-1])+abs(a[nn-1][nn-2]);
						y=x=0.75*s;
						w = -0.4375*s*s;
					}
					++its;
					for (m=nn-2;m>=l;m--) {
						z=a[m][m];
						r=x-z;
						s=y-z;
						p=(r*s-w)/a[m+1][m]+a[m][m+1];
						q=a[m+1][m+1]-z-r-s;
						r=a[m+2][m+1];
						s=abs(p)+abs(q)+abs(r);
						p /= s;
						q /= s;
						r /= s;
						if (m == l) break;
						u=abs(a[m][m-1])*(abs(q)+abs(r));
						v=abs(p)*(abs(a[m-1][m-1])+abs(z)+abs(a[m+1][m+1]));
						if (u <= EPS*v) break;
					}
					for (i=m;i<nn-1;i++) {
						a[i+2][i]=0.0;
						if (i != m) a[i+2][i-1]=0.0;
					}
					for (k=m;k<nn;k++) {
						if (k != m) {
							p=a[k][k-1];
							q=a[k+1][k-1];
							r=0.0;
							if (k+1 != nn) r=a[k+2][k-1];
							if ((x=abs(p)+abs(q)+abs(r)) != 0.0) {
								p /= x;
								q /= x;
								r /= x;
							}
						}
						if ((s=SIGN(sqrt(p*p+q*q+r*r),p)) != 0.0) {
							if (k == m) {
								if (l != m)
								a[k][k-1] = -a[k][k-1];
							} else
								a[k][k-1] = -s*x;
							p += s;
							x=p/s;
							y=q/s;
							z=r/s;
							q /= p;
							r /= p;
							for (j=k;j<nn+1;j++) {
								p=a[k][j]+q*a[k+1][j];
								if (k+1 != nn) {
									p += r*a[k+2][j];
									a[k+2][j] -= p*z;
								}
								a[k+1][j] -= p*y;
								a[k][j] -= p*x;
							}
							mmin = nn < k+3 ? nn : k+3;
							for (i=l;i<mmin+1;i++) {
								p=x*a[i][k]+y*a[i][k+1];
								if (k+1 != nn) {
									p += z*a[i][k+2];
									a[i][k+2] -= p*r;
								}
								a[i][k+1] -= p*q;
								a[i][k] -= p;
							}
						}
					}
				}
			}
		} while (l+1 < nn);
	}
}
void Unsymmeig::hqr2()
{
	Int nn,m,l,k,j,its,i,mmin,na;
	Doub z,y,x,w,v,u,t,s,r,q,p,anorm=0.0,ra,sa,vr,vi;

	const Doub EPS=numeric_limits<Doub>::epsilon();
	for (i=0;i<n;i++)
		for (j=MAX(i-1,0);j<n;j++)
			anorm += abs(a[i][j]);
	nn=n-1;
	t=0.0;
	while (nn >= 0) {
		its=0;
		do {
			for (l=nn;l>0;l--) {
				s=abs(a[l-1][l-1])+abs(a[l][l]);
				if (s == 0.0) s=anorm;
				if (abs(a[l][l-1]) <= EPS*s) {
					a[l][l-1] = 0.0;
					break;
				}
			}
			x=a[nn][nn];
			if (l == nn) {
				wri[nn]=a[nn][nn]=x+t;
				nn--;
			} else {
				y=a[nn-1][nn-1];
				w=a[nn][nn-1]*a[nn-1][nn];
				if (l == nn-1) {
					p=0.5*(y-x);
					q=p*p+w;
					z=sqrt(abs(q));
					x += t;
					a[nn][nn]=x;
					a[nn-1][nn-1]=y+t;
					if (q >= 0.0) {
						z=p+SIGN(z,p);
						wri[nn-1]=wri[nn]=x+z;
						if (z != 0.0) wri[nn]=x-w/z;
						x=a[nn][nn-1];
						s=abs(x)+abs(z);
						p=x/s;
						q=z/s;
						r=sqrt(p*p+q*q);
						p /= r;
						q /= r;
						for (j=nn-1;j<n;j++) {
							z=a[nn-1][j];
							a[nn-1][j]=q*z+p*a[nn][j];
							a[nn][j]=q*a[nn][j]-p*z;
						}
						for (i=0;i<=nn;i++) {
							z=a[i][nn-1];
							a[i][nn-1]=q*z+p*a[i][nn];
							a[i][nn]=q*a[i][nn]-p*z;
						}
						for (i=0;i<n;i++) {
							z=zz[i][nn-1];
							zz[i][nn-1]=q*z+p*zz[i][nn];
							zz[i][nn]=q*zz[i][nn]-p*z;
						}
					} else {
						wri[nn]=Complex(x+p,-z);
						wri[nn-1]=conj(wri[nn]);
					}
					nn -= 2;
				} else {
					if (its == 30) throw("Too many iterations in hqr");
					if (its == 10 || its == 20) {
						t += x;
						for (i=0;i<nn+1;i++) a[i][i] -= x;
						s=abs(a[nn][nn-1])+abs(a[nn-1][nn-2]);
						y=x=0.75*s;
						w = -0.4375*s*s;
					}
					++its;
					for (m=nn-2;m>=l;m--) {
						z=a[m][m];
						r=x-z;
						s=y-z;
						p=(r*s-w)/a[m+1][m]+a[m][m+1];
						q=a[m+1][m+1]-z-r-s;
						r=a[m+2][m+1];
						s=abs(p)+abs(q)+abs(r);
						p /= s;
						q /= s;
						r /= s;
						if (m == l) break;
						u=abs(a[m][m-1])*(abs(q)+abs(r));
						v=abs(p)*(abs(a[m-1][m-1])+abs(z)+abs(a[m+1][m+1]));
						if (u <= EPS*v) break;
					}
					for (i=m;i<nn-1;i++) {
						a[i+2][i]=0.0;
						if (i != m) a[i+2][i-1]=0.0;
					}
					for (k=m;k<nn;k++) {
						if (k != m) {
							p=a[k][k-1];
							q=a[k+1][k-1];
							r=0.0;
							if (k+1 != nn) r=a[k+2][k-1];
							if ((x=abs(p)+abs(q)+abs(r)) != 0.0) {
								p /= x;
								q /= x;
								r /= x;
							}
						}
						if ((s=SIGN(sqrt(p*p+q*q+r*r),p)) != 0.0) {
							if (k == m) {
								if (l != m)
								a[k][k-1] = -a[k][k-1];
							} else
								a[k][k-1] = -s*x;
							p += s;
							x=p/s;
							y=q/s;
							z=r/s;
							q /= p;
							r /= p;
							for (j=k;j<n;j++) {
								p=a[k][j]+q*a[k+1][j];
								if (k+1 != nn) {
									p += r*a[k+2][j];
									a[k+2][j] -= p*z;
								}
								a[k+1][j] -= p*y;
								a[k][j] -= p*x;
							}
							mmin = nn < k+3 ? nn : k+3;
							for (i=0;i<mmin+1;i++) {
								p=x*a[i][k]+y*a[i][k+1];
								if (k+1 != nn) {
									p += z*a[i][k+2];
									a[i][k+2] -= p*r;
								}
								a[i][k+1] -= p*q;
								a[i][k] -= p;
							}
							for (i=0; i<n; i++) {
								p=x*zz[i][k]+y*zz[i][k+1];
								if (k+1 != nn) {
									p += z*zz[i][k+2];
									zz[i][k+2] -= p*r;
								}
								zz[i][k+1] -= p*q;
								zz[i][k] -= p;
							}
						}
					}
				}
			}
		} while (l+1 < nn);
	}
	if (anorm != 0.0) {
		for (nn=n-1;nn>=0;nn--) {
			p=real(wri[nn]);
			q=imag(wri[nn]);
			na=nn-1;
			if (q == 0.0) {
				m=nn;
				a[nn][nn]=1.0;
				for (i=nn-1;i>=0;i--) {
					w=a[i][i]-p;
					r=0.0;
					for (j=m;j<=nn;j++)
						r += a[i][j]*a[j][nn];
					if (imag(wri[i]) < 0.0) {
						z=w;
						s=r;
					} else {
						m=i;
						
						if (imag(wri[i]) == 0.0) {
							t=w;
							if (t == 0.0)
								t=EPS*anorm;
							a[i][nn]=-r/t;
						} else {
							x=a[i][i+1];
							y=a[i+1][i];
							q=SQR(real(wri[i])-p)+SQR(imag(wri[i]));
							t=(x*s-z*r)/q;
							a[i][nn]=t;
							if (abs(x) > abs(z))
								a[i+1][nn]=(-r-w*t)/x;
							else
								a[i+1][nn]=(-s-y*t)/z;
						}
						t=abs(a[i][nn]);
						if (EPS*t*t > 1)
							for (j=i;j<=nn;j++)
								a[j][nn] /= t;
					}
				}
			} else if (q < 0.0) {
				m=na;
				if (abs(a[nn][na]) > abs(a[na][nn])) {
					a[na][na]=q/a[nn][na];
					a[na][nn]=-(a[nn][nn]-p)/a[nn][na];
				} else {
					Complex temp=Complex(0.0,-a[na][nn])/Complex(a[na][na]-p,q);
					a[na][na]=real(temp);
					a[na][nn]=imag(temp);
				}
				a[nn][na]=0.0;
				a[nn][nn]=1.0;
				for (i=nn-2;i>=0;i--) {
					w=a[i][i]-p;
					ra=sa=0.0;
					for (j=m;j<=nn;j++) {
						ra += a[i][j]*a[j][na];
						sa += a[i][j]*a[j][nn];
					}
					if (imag(wri[i]) < 0.0) {
						z=w;
						r=ra;
						s=sa;
					} else {
						m=i;
						if (imag(wri[i]) == 0.0) {
							Complex temp = Complex(-ra,-sa)/Complex(w,q);
							a[i][na]=real(temp);
							a[i][nn]=imag(temp);
						} else {
							x=a[i][i+1];
							y=a[i+1][i];
							vr=SQR(real(wri[i])-p)+SQR(imag(wri[i]))-q*q;
							vi=2.0*q*(real(wri[i])-p);
							if (vr == 0.0 && vi == 0.0)
								vr=EPS*anorm*(abs(w)+abs(q)+abs(x)+abs(y)+abs(z));
							Complex temp=Complex(x*r-z*ra+q*sa,x*s-z*sa-q*ra)/
								Complex(vr,vi);
							a[i][na]=real(temp);
							a[i][nn]=imag(temp);
							if (abs(x) > abs(z)+abs(q)) {
								a[i+1][na]=(-ra-w*a[i][na]+q*a[i][nn])/x;
								a[i+1][nn]=(-sa-w*a[i][nn]-q*a[i][na])/x;
							} else {
								Complex temp=Complex(-r-y*a[i][na],-s-y*a[i][nn])/
									Complex(z,q);
								a[i+1][na]=real(temp);
								a[i+1][nn]=imag(temp);
							}
						}
					}
					t=MAX(abs(a[i][na]),abs(a[i][nn]));
					if (EPS*t*t > 1)
						for (j=i;j<=nn;j++) {
							a[j][na] /= t;
							a[j][nn] /= t;
						}
				}
			}
		}
		for (j=n-1;j>=0;j--)
			for (i=0;i<n;i++) {
				z=0.0;
				for (k=0;k<=j;k++)
					z += zz[i][k]*a[k][j];
				zz[i][j]=z;
			}
	}
}
void Unsymmeig::sort()
{
	Int i;
	for (Int j=1;j<n;j++) {
		Complex x=wri[j];
		for (i=j-1;i>=0;i--) {
			if (real(wri[i]) >= real(x)) break;
			wri[i+1]=wri[i];
		}
		wri[i+1]=x;
	}
}
void Unsymmeig::sortvecs()
{
	Int i;
	VecDoub temp(n);
	for (Int j=1;j<n;j++) {
		Complex x=wri[j];
		for (Int k=0;k<n;k++)
			temp[k]=zz[k][j];
		for (i=j-1;i>=0;i--) {
			if (real(wri[i]) >= real(x)) break;
			wri[i+1]=wri[i];
			for (Int k=0;k<n;k++)
				zz[k][i+1]=zz[k][i];
		}
		wri[i+1]=x;
		for (Int k=0;k<n;k++)
			zz[k][i+1]=temp[k];
	}
}

extern "C"
JNIEXPORT void JNICALL Java_NROps_NROps_cnrfft
  (JNIEnv *env, jobject obj, jdoubleArray cdata, jint size,  jint ssign)
 
   {
    jdouble *ha = env->GetDoubleArrayElements(cdata, NULL);
     	    
    realft(ha, size, ssign);

    
    env->ReleaseDoubleArrayElements( cdata, ha, 0);
	
   }

extern "C"
 JNIEXPORT void JNICALL Java_NROps_NROps_nfourn
   (JNIEnv *env, jobject obj, jdoubleArray cdata, jintArray sizes, jint dim, jint ssign)
   {
   jdouble *da = env->GetDoubleArrayElements(cdata, NULL);
   jint *siz = env->GetIntArrayElements(sizes, NULL);
   
   fourn((double *)da, (int *)siz, (int)dim, (int)ssign);
   
   env->ReleaseDoubleArrayElements(cdata, da, 0);
   env->ReleaseIntArrayElements(sizes, siz, 0);
   }


// multiplies two column indexed matrixes, a(n1 X n2), b (n2 X n3) returning result in c

extern "C"
JNIEXPORT void JNICALL Java_NROps_NROps_mul 
 (JNIEnv *env, jobject obj, jdoubleArray a, jint n1, jint n2, 
	jdoubleArray b, jint n3, jdoubleArray c) {
	double *aa = (double *)env->GetDoubleArrayElements( a, NULL);
	double *bb = (double *)env->GetDoubleArrayElements( b, NULL);
	double *cc = (double *)env->GetDoubleArrayElements( c, NULL);
		
	double *pA;
	double *pB;
	double *pC;
	double smrowcol;
  /*  
  for (int i= 0;  i< n1;  i++) {
    for (int j = 0;  j < n3;  j++) {
	   double smrowcol = 0;
            
            for (int k = 0;  k <  n2; k++) { 
			  smrowcol += aa[i*n2+k]  * bb[k*n3+j];
			}
			cc[i *n3 + j ] = smrowcol;
	   }
     }
  */
  
  pC = cc;
  for (int i=0; i<n1; i++) {
 for (int j=0; j<n3; j++) {

	  smrowcol = 0;
	  pA = aa + i*n2;
	  pB = bb+j;
	  
	  for (int k=0; k<n2; k++) {
		smrowcol += *pA * *pB;
		pA++;
		//pB += n3;
		pB++;
		}
		*pC++ = smrowcol;
	}
  }
   
	env->ReleaseDoubleArrayElements(a, aa, 0);
	env->ReleaseDoubleArrayElements( b, bb, 0);
	env->ReleaseDoubleArrayElements(c, cc, 0);
	
	
}

 	
	
	
	