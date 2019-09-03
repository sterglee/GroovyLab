

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#include <memory>

#include <iostream>

#include "CCOps_CCOps.h"

#include <pthread.h>

struct complex {double re,im;};
typedef struct complex Cpx;

void trnm(double *a,int m);
void trncm(Cpx *s,int n);
void house(double *w,double *v,double *u,int m);
int qrevec(double *ev,double *v,double *d,int m);
int qreval(double *e,double *d,int m);
void hconj(Cpx *a,int m);
void chouse(Cpx *h,double *e,double *d,int m);
void chousv(Cpx *h,double *d, double *u,int m);
void qrecvc(double *e,Cpx *v,double *d,int m);
void cmmul(Cpx *a,Cpx *b,Cpx *c,int m);
void atou1(double *r,int i,int j);
void atovm(double *r,int i);
int qrbdu1(double *w,double *x,double *y,int k,double *z,int l);
void ldumat(double *x,double *y,int i,int k);
void ldvmat(double *x,double *y,int k);
int qrbdv(double *x,double *y,double *z,int i,double *w,int j);
int qrbdi(double *x,double *y,int l);

static unsigned int a=69069U,c=244045795U;
static unsigned int s,h,sbuf[256];

int *hist(double *x,int n,double xmin,double xmax,int kbin,double *bin)
{ int k,*p; double *pm,u;
  p=(int *)calloc(kbin+2,sizeof(int)); ++p;
  *bin=(xmax-xmin)/kbin;
  for(pm=x+n; x<pm ;++x){
    if(*x > xmax) k=kbin;
    else if ((u=(*x-xmin))<0.) k= -1;
    else k=u / *bin;
    *(p+k)+=1;
   }
  return p;
}

double psi(int m)
{ double s= -.577215664901533; int k;
  for(k=1; k<m ;++k) s+=1./k;
  return s;
}
double psih(double v)
{ double s= -1.963510026021423,r;
  for(r=.5; r<v ;r+=1.) s+=1./r;
  return s;
}

double *autcor(double *x,  int n,int lag)
{ double *p,*q,*pmax,*cf; int j;
  cf=(double *)calloc(lag+1,sizeof(double));
  for(p=x,pmax=x+n; p<pmax ;++p)
    for(q=p,j=0; j<=lag &&q>=x ;) *(cf+j++)+= *p* *q--;
  for(j=1; j<=lag ;) *(cf+j++)/= *cf;
  return cf;
}




double unfl()
{ int i;
  i=(int)(s>>24); s=sbuf[i];
  h=a*h+c; sbuf[i]=h;
  return s*2.328306436538696e-10;
}

void setunfl(unsigned int k)
{ int j;
  for(h=k,j=0; j<=256 ;++j){
    h=a*h+c;
    if(j<256) sbuf[j]=h; else s=h;
   }
}


double kbes(double v,double x)
{ double y,s,t,tp,f,a0=1.57079632679490;
  double gaml(double u),psi(int u),modf(double u,double *v);
  int p,k,m;
  if(x==0.) return HUGE_VAL;
  y=x-10.5; if(y>0.) y*=y; tp=25.+.185*v*v;
  if(y<tp && modf(v+.5,&t)!=0.){ y=1.5+.5*v;
    if(x<y){ x/=2.; m=x; tp=t=exp(v*log(x)-gaml(v+1.));
      if(modf(v,&y)==0.){ k=y; tp*=v;
        f=2.*log(x)-psi(1)-psi(k+1);
        t/=2.; if(!(k&1)) t= -t; s=f*t;
        for(p=1,x*=x;;++p){ f-=1./p+1./(v+=1.);
          t*=x/(p*v); s+=(y=t*f);
          if(p>m && fabs(y)<1.e-14) break; }
        if(k>0){ x= -x; s+=(t=1./(tp*2.));
          for(p=1,--k; k>0 ;++p,--k) s+=(t*=x/(p*k)); }
       }
      else{ f=1./(t*v*2.); t*=a0/sin(2.*a0*v); s=f-t;
        for(p=1,x*=x,tp=v;;++p){
          t*=x/(p*(v+=1.)); f*= -x/(p*(tp-=1.));
          s+=(y=f-t); if(p>m && fabs(y)<1.e-14) break; }
       }
     }
    else{ double tq,h,w,z,r;
      t=12./pow(x,.333); k=t*t; y=2.*(x+k);
      m=v; v-=m; tp=v*v-.25; v+=1.; tq=v*v-.25;
      for(s=h=1.,r=f=z=w=0.; k>0 ;--k,y-=2.){
        t=(y*h-(k+1)*z)/(k-1-tp/k); z=h; f+=(h=t);
        t=(y*s-(k+1)*w)/(k-1-tq/k); w=s; r+=(s=t);  }
      t=sqrt(a0/x)*exp(-x); s*=t/r; h*=t/f; x/=2.; if(m==0) s=h;
      for(k=1; k<m ;++k){ t=v*s/x+h; h=s; s=t; v+=1.;}
     }
   }
  else{ s=t=sqrt(a0/x); x*=2.;
    for(p=1,y=.5; (tp=fabs(t))>1.e-14 ;++p,y+=1.){
      t*=(v+y)*(v-y)/(p*x); if(y>v && fabs(t)>=tp) break; s+=t; }
    s*=exp(-x/2.);
   }
  return s;
}

double kspbes(int n,double x)
{ double y,s,t,v; int p;
  if(x==0.) return HUGE_VAL;
  s=t=exp(-x)/x; x*=2.; v=n+.5;
  for(p=1,y=.5; y<v ;++p,y+=1.){
    t*=(v+y)*(v-y)/(p*x); s+=t; }
  return s;
}

double nbes(double v,double x)
{ double y,s,t,tp,u,f,a0=3.14159265358979;
  double gaml(double r),psi(int r),modf(double r,double *a);
  int p,k,m;
  y=x-8.5; if(y>0.) y*=y; tp=v*v/4.+13.69;
  if(y<tp){ if(x==0.) return HUGE_VAL;
    x/=2.; m=x; u=t=exp(v*log(x)-gaml(v+1.));
    if(modf(v,&y)==0.){ k=y; u*=v;
      f=2.*log(x)-psi(1)-psi(k+1);
      t/=a0; x*= -x; s=f*t;
      for(p=1;;++p){ f-=1./p+1./(v+=1.);
        t*=x/(p*v); s+=(y=t*f); if(p>m && fabs(y)<1.e-13) break; }
      if(k>0){ x= -x; s-=(t=1./(u*a0));
        for(p=1,--k; k>0 ;++p,--k) s-=(t*=x/(p*k)); }
     }
    else{ f=1./(t*v*a0); t/=tan(a0*v); s=t-f;
      for(p=1,x*=x,u=v;;++p){
        t*= -x/(p*(v+=1.)); f*=x/(p*(u-=1.));
        s+=(y=t-f); if(p>m && fabs(y)<1.e-13) break; }
     }
   }
  else{ x*=2.; s=t=2./sqrt(x*a0); u=0.;
    for(p=1,y=.5; (tp=fabs(t))>1.e-14 ;++p,y+=1.){
      t*=(v+y)*(v-y)/(p*x); if(y>v && fabs(t)>tp) break;
      if(!(p&1)){ t= -t; s+=t;} else u+=t;
     }
    y=(x-(v+.5)*a0)/2.; s=sin(y)*s+cos(y)*u;
   }
  return s;
}


/*

     Overload a Householder left-factored matrix A with the first
     n columns of the Householder orthogonal matrix.

     void atou1(double *a,int m,int n)
       a = pointer to store of m by n input matrix A. Elements
           of A on and below the main diagonal specify the
           vectors of n Householder reflections (see note below).
           This array is overloaded by the first n columns of the
           Householder transformation matrix.
       m = number of rows in A
       n = number of columns in A
*/

void atou1(double *a,int m,int n)
{ double *p0,*p,*q,*w;
  int i,j,k,mm;
  double s,h;
  w=(double *)calloc(m,sizeof(double));
  p0=a+n*n-1; i=n-1; mm=m-n;
  if(mm==0){ *p0=1.; p0-=n+1; --i; ++mm;}
  for(; i>=0 ;--i,++mm,p0-=n+1){
    if(*p0!=0.){
      for(j=0,p=p0+n; j<mm ;p+=n) w[j++]= *p;
      h= *p0; *p0=1.-h;
      for(j=0,p=p0+n; j<mm ;p+=n) *p= -h*w[j++];
      for(k=i+1,q=p0+1; k<n ;++k){
	for(j=0,p=q+n,s=0.; j<mm ;p+=n) s+=w[j++]* *p;
	s*=h;
	for(j=0,p=q+n; j<mm ;p+=n) *p-=s*w[j++];
        *q++ = -s;
       }
     }
    else{
      *p0=1.;
      for(j=0,p=p0+n,q=p0+1; j<mm ;++j,p+=n) *p= *q++ =0.;
     }
   }
  free(w);
}


/*
Overload a Householder right-factored square matrix A with the
    Householder transformation matrix V.

     void atovm(double *v,int n)
       v = pointer to store for the n by n orthogonal
           output matrix V
       n = number of rows and columns in V

-------------------------------------------------------------------------------

     Individual Householder reflections are specified by a vector h.
     The corresponding orthogonal reflection matrix is given by

                     H = I - c* h~ .

     Input matrices store the vector, normalized to have its leading
     coefficient equal to one, and the normalization factor

                    c = 2/(h~*h) .

     Storage for the vectors is by column starting at the diagonal for
     a left transform, and by row starting at the superdiagonal for a
     right transform. The first location holds c followed by
     components 2 to k of the vector.

*/

void atovm(double *v,int n)
{ double *p0,*q0,*p,*q,*qq;
  double h,s;
  int i,j,k,mm;
  q0=v+n*n-1; *q0=1.; q0-=n+1;
  p0=v+n*n-n-n-1;
  for(i=n-2,mm=1; i>=0 ;--i,p0-=n+1,q0-=n+1,++mm){
    if(i && *(p0-1)!=0.){
      for(j=0,p=p0,h=1.; j<mm ;++j,++p) h+= *p* *p;
      h= *(p0-1); *q0=1.-h;
      for(j=0,q=q0+n,p=p0; j<mm ;++j,q+=n) *q= -h* *p++; 
      for(k=i+1,q=q0+1; k<n ;++k){
        for(j=0,qq=q+n,p=p0,s=0.; j<mm ;++j,qq+=n) s+= *qq* *p++;
        s*=h;
        for(j=0,qq=q+n,p=p0; j<mm ;++j,qq+=n) *qq-=s* *p++;
        *q++ = -s;
       }
     }
    else{
      *q0=1.;
      for(j=0,p=q0+1,q=q0+n; j<mm ;++j,q+=n) *q= *p++ =0.;
     }
   }
}


/*
Transform a Hermitian matrix H to real symmetric tridiagonal
     form.

     void chouse(Cpx *a,double *d,double *dp,int n)
       a = pointer to input array of complex matrix elements of H
           This array is altered by the computation
       d = pointer to output array of real diagonal elements
       dp = pointer to output array of real superdiagonal elements
       n = system dimension, with:
           dim(a) = n * n, dim(d) = dim(dn) = n;
*/
void chouse(Cpx *a,double *d,double *dp,int n)
{ double sc,x,y; Cpx cc,u,*q0;
  int i,j,k,m,e;
  Cpx *qw,*pc,*p;
  q0=(Cpx *)calloc(2*n,sizeof(Cpx));
  for(i=0,p=q0+n,pc=a; i<n ;++i,pc+=n+1) *p++ = *pc;
  for(j=0,pc=a; j<n-2 ;++j,pc+=n+1){
    m=n-j-1;
    for(i=1,sc=0.; i<=m ;++i) sc+=pc[i].re*pc[i].re+pc[i].im*pc[i].im;
    if(sc>0.){ sc=sqrt(sc); p=pc+1;
      y=sc+(x=sqrt(p->re*p->re+p->im*p->im));
      if(x>0.){ cc.re=p->re/x; cc.im=p->im/x;}
      else{ cc.re=1.; cc.im=0.;}
      x=1./sqrt(2.*sc*y); y*=x;
      for(i=0,qw=pc+1; i<m ;++i){
	q0[i].re=q0[i].im=0.;
	if(i){ qw[i].re*=x; qw[i].im*= -x;}
	else{ qw[0].re=y*cc.re; qw[0].im= -y*cc.im;}
       }
      for(i=0,e=j+2,p=pc+n+1,y=0.; i<m ;++i,p+=e++){
	q0[i].re+=(u.re=qw[i].re)*p->re - (u.im=qw[i].im)*p->im;
	q0[i].im+=u.re*p->im + u.im*p->re; ++p;
	for(k=i+1; k<m ;++k,++p){
	  q0[i].re+=qw[k].re*p->re - qw[k].im*p->im;
	  q0[i].im+=qw[k].im*p->re + qw[k].re*p->im;
	  q0[k].re+=u.re*p->re + u.im*p->im;
	  q0[k].im+=u.im*p->re - u.re*p->im;
         }
	y+=u.re*q0[i].re + u.im*q0[i].im;
       }
      for(i=0; i<m ;++i){
	q0[i].re-=y*qw[i].re; q0[i].re+=q0[i].re;
	q0[i].im-=y*qw[i].im; q0[i].im+=q0[i].im;
       }
      for(i=0,e=j+2,p=pc+n+1; i<m ;++i,p+=e++){
	for(k=i; k<m ;++k,++p){
	  p->re-=qw[i].re*q0[k].re + qw[i].im*q0[k].im
		 +q0[i].re*qw[k].re + q0[i].im*qw[k].im;
	  p->im-=qw[i].im*q0[k].re - qw[i].re*q0[k].im
		 +q0[i].im*qw[k].re - q0[i].re*qw[k].im;
	 }
       }
     }
    d[j]=pc->re; dp[j]=sc;
   }
  d[j]=pc->re; d[j+1]=(pc+n+1)->re;
  u= *(pc+1); dp[j]=sqrt(u.re*u.re+u.im*u.im);
  for(j=0,pc=a,qw=q0+n; j<n ;++j,pc+=n+1){
    *pc= qw[j];
    for(i=1,p=pc+n; i<n-j ;++i,p+=n){
      pc[i].re=p->re; pc[i].im= -p->im;
     }
   }
  free(q0);
}

/*
Compute the transpose A = B~ of a complex m by n matrix.

     void cmattr(Cpx *a,Cpx *b,int m,int n)
       a = pointer to output array of matrix A
       b = pointer to input array of matrix B
       m, n = matrix dimensions, with  B m by n and A n by m
               (dim(a)=dim(b)= m*n)

*/    

void cmattr(Cpx *a,Cpx *b,int m,int n)
{ Cpx *p; int i,j;
  for(i=0; i<n ;++i,++b)
    for(j=0,p=b; j<m ;++j,p+=n) *a++ = *p;
}


/*
Copy a complex array A = B.

     void cmcpy(Cpx *a,Cpx *b,int n)
       a = pointer to store for output array
       b = pointer to store for input array
       n = dimension of complex arrays A and B
*/

void cmcpy(Cpx *a,Cpx *b,int n)
{ int i;
  for(i=0; i<n ;++i) *a++ = *b++;
}


/*
Invert a general complex matrix in place A -> Inv(A).

     int cminv(Cpx *a,int n)
       a = pointer to input array of complex n by n matrix A
           The computation replaces A by its inverse.
       n = dimension of system (dim(a)=n*n)
      return value: status flag with: 0 -> valid solution
                                      1 -> system singular
*/
int cminv(Cpx *a,int n)
{ int i,j,k,m,lc,*le; Cpx *ps,*p,*q,*pa,*pd;
  Cpx z,h,*q0; double s,t,tq=0.,zr=1.e-15;
  le=(int *)calloc(n,sizeof(int));
  q0=(Cpx *)calloc(n,sizeof(Cpx));
  pa=pd=a;
  for(j=0; j<n ;++j,++pa,pd+=n+1){
    if(j>0){
      for(i=0,p=pa,q=q0; i<n ;++i,p+=n) *q++ = *p;
      for(i=1; i<n ;++i){ lc=i<j?i:j;
        z.re=z.im=0.;
        for(k=0,p=pa+i*n-j,q=q0; k<lc ;++k,++q,++p){
	  z.re+=p->re*q->re-p->im*q->im;
	  z.im+=p->im*q->re+p->re*q->im;
         }
	q0[i].re-=z.re; q0[i].im-=z.im;
       }
      for(i=0,p=pa,q=q0; i<n ;++i,p+=n) *p= *q++;
     }
    s=fabs(pd->re)+fabs(pd->im); lc=j;
    for(k=j+1,ps=pd; k<n ;++k){ ps+=n;
      if((t=fabs(ps->re)+fabs(ps->im))>s){ s=t; lc=k;}
     }
    tq=tq>s?tq:s; if(s<zr*tq){ free(le-j); free(q0); return -1;}
    *le++ =lc;
    if(lc!=j){ p=a+n*j; q=a+n*lc;
      for(k=0; k<n ;++k,++p,++q){ h= *p; *p= *q; *q=h;}
     }
    t=pd->re*pd->re+pd->im*pd->im;
    h.re=pd->re/t; h.im= -(pd->im)/t;
    for(k=j+1,ps=pd+n; k<n ;++k,ps+=n){
      z.re=ps->re*h.re-ps->im*h.im;
      z.im=ps->im*h.re+ps->re*h.im; *ps=z;
     }
    *pd=h;
   }
  for(j=1,pd=ps=a; j<n ;++j){
    for(k=0,pd+=n+1,q= ++ps; k<j ;++k,q+=n){
      z.re=q->re*pd->re-q->im*pd->im;
      z.im=q->im*pd->re+q->re*pd->im; *q=z;
     }
   }
  for(j=1,pa=a; j<n ;++j){ ++pa;
    for(i=0,q=q0,p=pa; i<j ;++i,p+=n) *q++ = *p;
    for(k=0; k<j ;++k){ h.re=h.im=0.;
      for(i=k,p=pa+k*n+k-j,q=q0+k; i<j ;++i){
	h.re-=p->re*q->re-p->im*q->im;
	h.im-=p->im*q->re+p->re*q->im; ++p; ++q;
       }
      q0[k]=h;
     }
    for(i=0,q=q0,p=pa; i<j ;++i,p+=n) *p= *q++;
   }
  for(j=n-2,pd=pa=a+n*n-1; j>=0 ;--j){ --pa; pd-=n+1;
    for(i=0,m=n-j-1,q=q0,p=pd+n; i<m ;++i,p+=n) *q++ = *p;
    for(k=n-1,ps=pa; k>j ;--k,ps-=n){
      z.re= -ps->re; z.im= -ps->im;
      for(i=j+1,p=ps+1,q=q0; i<k ;++i,++p,++q){
	z.re-=p->re*q->re-p->im*q->im;
	z.im-=p->im*q->re+p->re*q->im;
       }
      q0[--m]=z;
     }
    for(i=0,m=n-j-1,q=q0,p=pd+n; i<m ;++i,p+=n) *p= *q++;
   }
  for(k=0,pa=a; k<n-1 ;++k,++pa){
    for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *q++ = *p;
    for(j=0,ps=a; j<n ;++j,ps+=n){
      if(j>k){ h.re=h.im=0.; p=ps+j; i=j;}
      else{ h=q0[j]; p=ps+k+1; i=k+1;}
      for(; i<n ;++i,++p){
        h.re+=p->re*q0[i].re-p->im*q0[i].im;
	h.im+=p->im*q0[i].re+p->re*q0[i].im;
       }
      q0[j]=h;
     }
    for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *p= *q++;
   }
  for(j=n-2,le--; j>=0 ;--j){
    for(k=0,p=a+j,q=a+ *(--le); k<n ;++k,p+=n,q+=n){
      h= *p; *p= *q; *q=h;
     }
   }
  free(le); free(q0);
  return 0;
}


/*
Multiply two square complex matrices C = A * B.

     void cmmul(Cpx *c,Cpx *a,Cpx *b,int n)
       a = pointer to input array of left matrix factor A
       b = pointer to input array of right matrix factor B
       c = pointer to array of output product matrix C
       n = dimension parameter (dim(c)=dim(a)=dim(b)=n*n)
*/
void cmmul(Cpx *c,Cpx *a,Cpx *b,int n)
{ Cpx s,*p,*q; int i,j,k;
  trncm(b,n);
  for(i=0; i<n ;++i,a+=n){
    for(j=0,q=b; j<n ;++j){
      for(k=0,p=a,s.re=s.im=0.; k<n ;++k){
	s.re+=p->re*q->re-p->im*q->im;
	s.im+=p->im*q->re+p->re*q->im; ++p; ++q;
       }
      *c++ =s;
     }
   }
  trncm(b,n);
}


/*
Transform a Hermitian matrix H to real symmetric tridiagonal
     form, and compute the unitary matrix of this transformation.

     void chousv(Cpx *a,double *d,double *dp,int n)
       a = pointer to input array of complex matrix elements of H
           The computation replaces this with the unitary matrix
           U of the transformation.
       d = pointer to output array of real diagonal elements
       dp = pointer to output array of real superdiagonal elements
       n = system dimension, with:
           dim(a) = n*n, dim(d) = dim(dn) = n;

     The matrix U satisfies

          A = U^*T*U  where T is real and tridiagonal,
          with  T[i,i+1] = T[i+1,i] = dp[i]  and T[i,i] = d[i].

*/     

void chousv(Cpx *a,double *d,double *dp,int n)
{
 double sc,x,y; Cpx cc,u,*qs;
  int i,j,k,m,e;
  Cpx *qw,*pc,*p,*q;
  qs=(Cpx *)calloc(2*n,sizeof(Cpx)); q=qs+n;
  for(j=0,pc=a; j<n-2 ;++j,pc+=n+1,++q){
    m=n-j-1;
    for(i=1,sc=0.; i<=m ;++i) sc+=pc[i].re*pc[i].re+pc[i].im*pc[i].im;
    if(sc>0.){ sc=sqrt(sc); p=pc+1;
      y=sc+(x=sqrt(p->re*p->re+p->im*p->im));
      if(x>0.){ cc.re=p->re/x; cc.im=p->im/x;}
      else{ cc.re=1.; cc.im=0.;}
      q->re= -cc.re; q->im= -cc.im;
      x=1./sqrt(2.*sc*y); y*=x;
      for(i=0,qw=pc+1; i<m ;++i){
	qs[i].re=qs[i].im=0.;
	if(i){ qw[i].re*=x; qw[i].im*= -x;}
	else{ qw[0].re=y*cc.re; qw[0].im= -y*cc.im;}
       }
      for(i=0,e=j+2,p=pc+n+1,y=0.; i<m ;++i,p+=e++){
	qs[i].re+=(u.re=qw[i].re)*p->re - (u.im=qw[i].im)*p->im;
	qs[i].im+=u.re*p->im + u.im*p->re; ++p;
	for(k=i+1; k<m ;++k,++p){
	  qs[i].re+=qw[k].re*p->re - qw[k].im*p->im;
	  qs[i].im+=qw[k].im*p->re + qw[k].re*p->im;
	  qs[k].re+=u.re*p->re + u.im*p->im;
	  qs[k].im+=u.im*p->re - u.re*p->im;
         }
	y+=u.re*qs[i].re + u.im*qs[i].im;
       }
      for(i=0; i<m ;++i){
	qs[i].re-=y*qw[i].re; qs[i].re+=qs[i].re;
	qs[i].im-=y*qw[i].im; qs[i].im+=qs[i].im;
       }
      for(i=0,e=j+2,p=pc+n+1; i<m ;++i,p+=e++){
	for(k=i; k<m ;++k,++p){
	  p->re-=qw[i].re*qs[k].re + qw[i].im*qs[k].im
		 +qs[i].re*qw[k].re + qs[i].im*qw[k].im;
	  p->im-=qw[i].im*qs[k].re - qw[i].re*qs[k].im
		 +qs[i].im*qw[k].re - qs[i].re*qw[k].im;
	 }
       }
     }
    d[j]=pc->re; dp[j]=sc;
   }
  d[j]=pc->re; cc= *(pc+1); d[j+1]=(pc+=n+1)->re;
  dp[j]=sc=sqrt(cc.re*cc.re+cc.im*cc.im);
  q->re=cc.re/=sc; q->im=cc.im/=sc;
  for(i=0,m=n+n,p=pc; i<m ;++i,--p) p->re=p->im=0.;
  pc->re=1.; (pc-=n+1)->re=1.; qw=pc-n;
  for(m=2; m<n ;++m,qw-=n+1){
    for(j=0,p=pc,pc->re=1.; j<m ;++j,p+=n){
      for(i=0,q=p,u.re=u.im=0.; i<m ;++i,++q){
	u.re+=qw[i].re*q->re-qw[i].im*q->im;
	u.im+=qw[i].re*q->im+qw[i].im*q->re;
       }
      for(i=0,q=p,u.re+=u.re,u.im+=u.im; i<m ;++i,++q){
	q->re-=u.re*qw[i].re+u.im*qw[i].im;
	q->im-=u.im*qw[i].re-u.re*qw[i].im;
       }
     }
    for(i=0,p=qw+m-1; i<n ;++i,--p) p->re=p->im=0.;
    (pc-=n+1)->re=1.;
   }
  for(j=1,p=a+n+1,q=qs+n,u.re=1.,u.im=0.; j<n ;++j,++p,++q){
    sc=u.re*q->re-u.im*q->im; u.im=u.im*q->re+u.re*q->im; u.re=sc;
    for(i=1; i<n ;++i,++p){
      sc=u.re*p->re-u.im*p->im; p->im=u.re*p->im+u.im*p->re; p->re=sc;
     }
   }
  free(qs);
}




/*
Perform a QR reduction of a bidiagonal matrix and update the
     the orthogonal transformation matrices U and V.

     int qrbdv(double *d,double *e,double *u,int m,double *v,int n)
       d = pointer to n-dimensional array of diagonal values
           (overloaded by diagonal elements of reduced matrix)
       e = pointer to store of superdiagonal values (loaded in
           first m-1 elements of the array). Values are altered
           by the computation.
       u = pointer to store of m by m orthogonal matrix U updated
           by the computation
       v = pointer to store of n by n orthogonal matrix V updated
           by the computation
       m = dimension parameter of the U matrix
       n = size of the d and e arrays and the number of rows and
           columns in the V matrix
      return value: N = number of QR iterations required
*/
int qrbdv(double *dm,double *em,double *um,int mm,double *vm,int m)
{ int i,j,k,n,jj,nm;
  double u,x,y,a,b,c,s,t,w,*p,*q;
  for (j=1,t=fabs(dm[0]); j<m ;++j)
    if((s=fabs(dm[j])+fabs(em[j-1]))>t) t=s;
  t*=1.e-15; n=100*m; nm=m;
  for(j=0; m>1 && j<n ;++j){
    for(k=m-1; k>0 ;--k){
      if(fabs(em[k-1])<t) break;
      if(fabs(dm[k-1])<t){
        for(i=k,s=1.,c=0.; i<m ;++i){
          a=s*em[i-1]; b=dm[i]; em[i-1]*=c;
          dm[i]=u=sqrt(a*a+b*b); s= -a/u; c=b/u;
          for(jj=0,p=um+k-1; jj<mm ;++jj,p+=mm){
            q=p+i-k+1;
            w=c* *p+s* *q; *q=c* *q-s* *p; *p=w;
           }
	     }
        break;
       }
     }
    y=dm[k]; x=dm[m-1]; u=em[m-2];
    a=(y+x)*(y-x)-u*u; s=y*em[k]; b=s+s;
    u=sqrt(a*a+b*b);
    if(u!=0.){
      c=sqrt((u+a)/(u+u));
	  if(c!=0.) s/=(c*u); else s=1.;
	  for(i=k; i<m-1 ;++i){
        b=em[i];
        if(i>k){
          a=s*em[i]; b*=c;
	      em[i-1]=u=sqrt(x*x+a*a);
    	  c=x/u; s=a/u;
         }
        a=c*y+s*b; b=c*b-s*y;
        for(jj=0,p=vm+i; jj<nm ;++jj,p+=nm){
          w=c* *p+s* *(p+1); *(p+1)=c* *(p+1)-s* *p; *p=w;
         }
        s*=dm[i+1]; dm[i]=u=sqrt(a*a+s*s);
        y=c*dm[i+1]; c=a/u; s/=u;
        x=c*b+s*y; y=c*y-s*b;
        for(jj=0,p=um+i; jj<mm ;++jj,p+=mm){
          w=c* *p+s* *(p+1); *(p+1)=c* *(p+1)-s* *p; *p=w;
         }
	   }
     }
    em[m-2]=x; dm[m-1]=y;
    if(fabs(x)<t) --m;
    if(m==k+1) --m; 
   }
  return j;
}


/*
Compute a left Householder transform matrix U from the vectors
     specifying the Householder reflections.

     void ldumat(double *a,double *u,int m,int n)
       a = pointer to store of m by n input matrix A. Elements
           of A on and below the main diagonal specify the
           vectors of n Householder reflections (see note below).
       u = pointer to store for the m by m orthogonal output
           matrix U.
       m = number of rows in A and U, and number of columns in U.
       n = number of columns in A

*/     
void ldumat(double *a,double *u,int m,int n)
{ double *p0,*q0,*p,*q,*w;
  int i,j,k,mm;
  double s,h;
  w=(double *)calloc(m,sizeof(double));
  for(i=0,mm=m*m,q=u; i<mm ;++i) *q++ =0.;
  p0=a+n*n-1; q0=u+m*m-1; mm=m-n; i=n-1;
  for(j=0; j<mm ;++j,q0-=m+1) *q0=1.;
  if(mm==0){ p0-=n+1; *q0=1.; q0-=m+1; --i; ++mm;}
  for(; i>=0 ;--i,++mm,p0-=n+1,q0-=m+1){
    if(*p0!=0.){
      for(j=0,p=p0+n,h=1.; j<mm ;p+=n) w[j++]= *p;
      h= *p0; *q0=1.-h;
      for(j=0,q=q0+m; j<mm ;q+=m) *q= -h*w[j++];
      for(k=i+1,q=q0+1; k<m ;++k){
	for(j=0,p=q+m,s=0.; j<mm ;p+=m) s+=w[j++]* *p;
	s*=h;
	for(j=0,p=q+m; j<mm ;p+=m) *p-=s*w[j++];
        *q++ = -s;
       }
     }
    else{
      *q0=1.;
      for(j=0,p=q0+1,q=q0+m; j<mm ;++j,q+=m) *q= *p++ =0.;
     }
   }
  free(w);
}

/*
     Compute a right Householder transform matrix from the vectors
     specifying the Householder reflections.

     void ldvmat(double *a,double *v,int n)
       a = pointer to store of n by n input matrix A. Elements
           of A on and above the superdiagonal specify vectors
           of a sequence of Householder reflections (see note below).
       v = pointer to store for the n by n orthogonal output
           matrix V
       n = number of rows and columns in A and V
*/

void ldvmat(double *a,double *v,int n)
{
 double *p0,*q0,*p,*q,*qq;
  double h,s;
  int i,j,k,mm;
  for(i=0,mm=n*n,q=v; i<mm ;++i) *q++ =0.;
  *v=1.; q0=v+n*n-1; *q0=1.; q0-=n+1;
  p0=a+n*n-n-n-1;
  for(i=n-2,mm=1; i>0 ;--i,p0-=n+1,q0-=n+1,++mm){
    if(*(p0-1)!=0.){
      for(j=0,p=p0,h=1.; j<mm ;++j,++p) h+= *p* *p;
      h= *(p0-1); *q0=1.-h;
      for(j=0,q=q0+n,p=p0; j<mm ;++j,q+=n) *q= -h* *p++; 
      for(k=i+1,q=q0+1; k<n ;++k){
        for(j=0,qq=q+n,p=p0,s=0.; j<mm ;++j,qq+=n) s+= *qq* *p++;
        s*=h;
        for(j=0,qq=q+n,p=p0; j<mm ;++j,qq+=n) *qq-=s* *p++;
        *q++ = -s;
       }
     }
    else{
      *q0=1.;
      for(j=0,p=q0+1,q=q0+n; j<mm ;++j,q+=n) *q= *p++ =0.;
     }
   }
}


/*      Compute the singular value transformation S = U~*A*V.

     int svduv(double *d,double *a,double *u, int m, double *v, int n)
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (A is altered by the computation)
       u = pointer to store for m by m orthogonal matrix U
       v = pointer to store for n by n orthogonal matrix V
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/

int svduv(double *d,double *a,double *u, int m, double *v, int n)
{ 
 double *p,*p1,*q,*pp,*w,*e;
  double s,h,r,t,sv;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m+n,sizeof(double)); e=w+m;
  for(i=0,mm=m,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r*=s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(j=1,q=p; j<mm ;) *(q+=n)=t*w[j++];
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
    p1=p+1; sv=h=0.;
    if(nm>1){
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
        sv=1.+fabs(*p1/h);
	s+= *p1*h; s=1./s; t=1./(*p1+=h);
	for(k=n,ms=n*(m-i); k<ms ;k+=n){
	  for(j=0,q=p1,pp=p1+k,r=0.; j<nm ;++j) r+= *q++ * *pp++;
	  r*=s;
	  for(j=0,q=p1,pp=p1+k; j<nm ;++j) *pp++ -=r* *q++;
	 }
        for(j=1,q=p1+1; j<nm ;++j) *q++ *=t;
       }
      *p1=sv; e[i]= -h;
     }
    if(nm==1) e[i]= *p1;
   }
  ldvmat(a,v,n); ldumat(a,u,m,n);
  qrbdv(d,e,u,m,v,n);
  for(i=0; i<n ;++i){
    if(d[i]<0.){ d[i]= -d[i];
      for(j=0,p=v+i; j<n ;++j,p+=n) *p= - *p;
     }
   }
  free(w);
  return 0;
}

/*
Solve a general linear system  A*x = b.

     int solv(double a[],double b[],int n)
       a = array containing system matrix A in row order
            (altered to L-U factored form by computation)
       b = array containing system vector b at entry and
           solution vector x at exit
       n = dimension of system
      return:  0 -> normal exit
              -1 -> singular input
*/
int solv(double *a,double *b,int n)
{ int i,j,k,lc; double *ps,*p,*q,*pa,*pd;
  double *q0,s,t,tq=0.,zr=1.e-15;
  q0=(double *)calloc(n,sizeof(double));
  for(j=0,pa=a,pd=a; j<n ;++j,++pa,pd+=n+1){
    if(j){
      for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *q++ = *p;
      for(i=1; i<n ;++i){ lc=i<j?i:j;
        for(k=0,p=pa+i*n-j,q=q0,t=0.; k<lc ;++k) t+= *p++ * *q++;
	q0[i]-=t;
       }
      for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *p= *q++;
     }
    s=fabs(*pd); lc=j;
    for(k=j+1,ps=pd; k<n ;++k){
      if((t=fabs(*(ps+=n)))>s){ s=t; lc=k;}
     }
    tq=tq>s?tq:s; if(s<zr*tq){ free(q0); return -1;}
    if(lc!=j){ t=b[j]; b[j]=b[lc]; b[lc]=t;
      for(k=0,p=a+n*j,q=a+n*lc; k<n ;++k){
        t= *p; *p++ = *q; *q++ =t;
       }
     }
    for(k=j+1,ps=pd,t=1./ *pd; k<n ;++k) *(ps+=n)*=t;
   }
  for(j=1,ps=b+1; j<n ;++j){
    for(k=0,p=a+n*j,q=b,t=0.; k<j ;++k) t+= *p++ * *q++;
    *ps++ -=t;
   }
  for(j=n-1,--ps,pd=a+n*n-1; j>=0 ;--j,pd-=n+1){
    for(k=j+1,p=pd,q=b+j,t=0.; k<n ;++k) t+= *++p * *++q;
    *ps-=t; *ps-- /= *pd;
   }
  free(q0); return 0;
}


/*
Solve a complex linear system A*x = b.

     int csolv(Cpx *a,Cpx *b,int n)
       a = pointer to array of n by n system matrix A
           The computation alters this array to a LU factorization.
       b = pointer to input array of system vector b
           This is replaced by the solution vector b -> x.
       n = dimension of system (dim(a)=n*n, dim(b)=n)
      return value: status flag with: 0 -> valid solution
                                      1 -> system singular
*/
int csolv(Cpx *a,Cpx *b,int n)
{ int i,j,k,lc; Cpx *ps,*p,*q,*pa,*pd;
  Cpx z,h,*q0; double s,t,tq=0.,zr=1.e-15;
  q0=(Cpx *)calloc(n,sizeof(Cpx));
  pa=a; pd=a;
  for(j=0; j<n ;++j,++pa,pd+=n+1){
    if(j>0){
      for(i=0,p=pa,q=q0; i<n ;++i,p+=n) *q++ = *p;
      for(i=1; i<n ;++i){ lc=i<j?i:j;
        z.re=z.im=0.;
        for(k=0,p=pa+i*n-j,q=q0; k<lc ;++k,++q,++p){
	  z.re+=p->re*q->re-p->im*q->im;
	  z.im+=p->im*q->re+p->re*q->im;
         }
	q0[i].re-=z.re; q0[i].im-=z.im;
       }
      for(i=0,p=pa,q=q0; i<n ;++i,p+=n) *p= *q++;
     }
    s=fabs(pd->re)+fabs(pd->im); lc=j;
    for(k=j+1,ps=pd; k<n ;++k){ ps+=n;
      if((t=fabs(ps->re)+fabs(ps->im))>s){ s=t; lc=k;}
     }
    tq=tq>s?tq:s; if(s<zr*tq){ free(q0); return -1;}
    if(lc!=j){
      h=b[j]; b[j]=b[lc]; b[lc]=h;
      p=a+n*j; q=a+n*lc;
      for(k=0; k<n ;++k){ h= *p; *p++=*q; *q++=h;}
     }
    t=pd->re*pd->re+pd->im*pd->im;
    h.re=pd->re/t; h.im= -(pd->im)/t;
    for(k=j+1,ps=pd+n; k<n ;++k,ps+=n){
      z.re=ps->re*h.re-ps->im*h.im;
      z.im=ps->im*h.re+ps->re*h.im; *ps=z;
     }
   }
  for(j=1,ps=b+1; j<n ;++j,++ps){
    for(k=0,p=a+n*j,q=b,z.re=z.im=0.; k<j ;++k){
      z.re+=p->re*q->re-p->im*q->im;
      z.im+=p->im*q->re+p->re*q->im; ++p; ++q;
     }
    ps->re-=z.re; ps->im-=z.im;
   } 
  for(j=n-1,--ps,pd=a+n*n-1; j>=0 ;--j,pd-=n+1){
    for(k=j+1,p=pd+1,q=b+j+1,z.re=z.im=0.; k<n ;++k){
      z.re+=p->re*q->re-p->im*q->im;
      z.im+=p->im*q->re+p->re*q->im; ++p; ++q;
     }
    h.re=ps->re-z.re; h.im=ps->im-z.im;
    t=pd->re*pd->re+pd->im*pd->im;
    ps->re=(h.re*pd->re+h.im*pd->im)/t;
    ps->im=(h.im*pd->re-h.re*pd->im)/t; --ps;
   }
  free(q0); return 0;
}


/*
Transform a complex vector  u = A*v.

     void cvmul(Cpx *u,Cpx *a,Cpx *v,int n)
       u = pointer to array of output vector u.
       a = pointer to array of transform matrix A.
       v = pointer to array of input vector v.
       n = dimension (dim(u)=dim(v)=n, dim(a)=n*n)

*/     

void cvmul(Cpx *u,Cpx *a,Cpx *v,int n)
{ Cpx *q; int i,j;
  for(i=0; i<n ;++i,++u){
    u->re=u->im=0.;
    for(j=0,q=v; j<n ;++j,++a,++q){
      u->re+=a->re*q->re-a->im*q->im;
      u->im+=a->im*q->re+a->re*q->im;
     }
   }
}

/*
Compute a Hermitian inner product s = u^*v.

     Cpx cvnrm(Cpx *u,Cpx *v,int n)
       u = pointer to array of first vector u
       v = pointer to array of second vector v
       n = dimension (dim(u)=dim(v)=n)
      return value: s = complex value of inner product
*/
Cpx cvnrm(Cpx *u,Cpx *v,int n)
{ int k; Cpx z;
  z.re=z.im=0.;
  for(k=0; k<n ;++k,++u,++v){
    z.re+=u->re*v->re+u->im*v->im;
    z.im+=u->re*v->im-u->im*v->re;
   }
  return z;
}

/*
     Multiply two complex matrices C = A * B.

     void cmmult(Cpx *c,Cpx *a,Cpx *b,int n,int m,int l)
       a = pointer to input array of right n by m factor matrix A
       b = pointer to input array of left m by l factor matrix B
       c = pointer to store for n by l output matrix C
       n,m,l = system dimension parameters, with
                 (dim(c)=n*l, dim(a)=n*m, dim(b)=m*l)

*/
void cmmult(Cpx *cm,Cpx *a,Cpx *b,int n,int m,int l)
{ Cpx z,*q0,*p,*q; int i,j,k;
  q0=(Cpx *)calloc(m,sizeof(Cpx));
  for(i=0; i<l ;++i,++cm){
    for(k=0,p=b+i; k<m ;p+=l) q0[k++]= *p;
    for(j=0,p=a,q=cm; j<n ;++j,q+=l){
      for(k=0,z.re=z.im=0.; k<m ;++k,++p){
	z.re+=p->re*q0[k].re-p->im*q0[k].im;
	z.im+=p->im*q0[k].re+p->re*q0[k].im;
       }
      *q=z;
     }
   }
  free(q0);
}


/*
     Print rows of a complex matrix in a specified format.

     void cmprt(Cpx *a,int m,int n,char *f)
       a = pointer to array of complex m by n matrix
       m = number of columns
       n = number of rows
       f = character array holding format for complex number
           output  (ie., "%f, %f  ")

     Long rows may overflow the print line.
*/
void cmprt(Cpx *a,int m,int n,char *f)
{ int i,j; Cpx *p;
  for(i=0,p=a; i<m ;++i){
    for(j=0; j<n ;++j,++p) printf(f,p->re,p->im);
    printf("\n");
   }
}

/*
     Transform a real symmetric matrix to tridiagonal form and
     compute the orthogonal matrix of this transformation.

     void housev(double *a,double *d,double *dp,int n)
       a = pointer to array of symmetric input matrix A
           The computation overloads this array with the
           orthogonal transformation matrix O.
       d = pointer to array of diagonal output elements
       dp = pointer to array of n-1 elements neighboring the
            diagonal in the symmetric transformed matrix
       n = dimension (dim(a)= n*n, dim(d)=dim(dp)=n)

     The orthogonal transformation matrix O satisfies  O~*T*O = A.
*/

void housev(double *a,double *d,double *dp,int n)
{ double sc,x,y,h;
  int i,j,k,m,e;
  double *qw,*qs,*pc,*p;
  qs=(double *)calloc(n,sizeof(double));
  for(j=0,pc=a; j<n-2 ;++j,pc+=n+1){
    m=n-j-1;
    for(i=1,sc=0.; i<=m ;++i) sc+=pc[i]*pc[i];
    if(sc>0.){ sc=sqrt(sc);
      if((x= *(pc+1))<0.){ y=x-sc; h=1./sqrt(-2.*sc*y);}
      else{ y=x+sc; h=1./sqrt(2.*sc*y); sc= -sc;}
      for(i=0,qw=pc+1; i<m ;++i){
        qs[i]=0.; if(i) qw[i]*=h; else qw[i]=y*h;
       }
      for(i=0,e=j+2,p=pc+n+1,h=0.; i<m ;++i,p+=e++){
        qs[i]+=(y=qw[i])* *p++;
	for(k=i+1; k<m ;++k){
          qs[i]+=qw[k]* *p; qs[k]+=y* *p++;
         }
        h+=y*qs[i];
       }
      for(i=0; i<m ;++i){
	qs[i]-=h*qw[i]; qs[i]+=qs[i];
       }
      for(i=0,e=j+2,p=pc+n+1; i<m ;++i,p+=e++){
        for(k=i; k<m ;++k) *p++ -=qw[i]*qs[k]+qs[i]*qw[k];
       }
     }
    d[j]= *pc; dp[j]=sc;
   }
  d[j]= *pc; dp[j]= *(pc+1); d[j+1]= *(pc+=n+1);
  free(qs);
  for(i=0,m=n+n,p=pc; i<m ;++i) *p-- =0.;
  *pc=1.; *(pc-=n+1)=1.; qw=pc-n;
  for(m=2; m<n ;++m,qw-=n+1){
    for(j=0,p=pc,*pc=1.; j<m ;++j,p+=n){
      for(i=0,qs=p,h=0.; i<m ;) h+=qw[i++]* *qs++;
      for(i=0,qs=p,h+=h; i<m ;) *qs++ -=h*qw[i++];
     }
    for(i=0,p=qw+m; i<n ;++i) *(--p)=0.;
    *(pc-=n+1)=1.;
   }
}

/*
Compute the eigenvalues and eigenvectors of a real symmetric
     matrix A.

     void eigen(double *a,double *ev,int n)
     double *a,*ev; int n;
       a = pointer to store for symmetric n by n input
           matrix A. The computation overloads this with an
           orthogonal matrix of eigenvectors E.
       ev = pointer to the array of the output eigenvalues
       n = dimension parameter (dim(a)= n*n, dim(ev)= n)

     The input and output matrices are related by

          A = E*D*E~ where D is the diagonal matrix of eigenvalues
          D[i,j] = ev[i] if i=j and 0 otherwise.

     The columns of E are the eigenvectors.
*/

void eigen(double *a,double *ev,int n)
{ double *dp;
  dp=(double *)calloc(n,sizeof(double));
  housev(a,ev,dp,n);
  qrevec(ev,a,dp,n); trnm(a,n);
  free(dp);
}

/*

     Compute the eigenvalues of a real symmetric matrix A.

     void eigval(double *a,double *ev,int n)
       a = pointer to array of symmetric n by n input
           matrix A. The computation alters these values.
       ev = pointer to array of the output eigenvalues
       n = dimension parameter (dim(a)= n*n, dim(ev)= n)
*/

void eigval(double *a,double *ev,int n)
{ double *dp;
  dp=(double *)calloc(n,sizeof(double));
  house(a,ev,dp,n);
  qreval(ev,dp,n);
  free(dp);
}


/* Compute the maximum (absolute) eigenvalue and corresponding
     eigenvector of a real symmetric matrix A.

     double evmax(double a[],double u[],int n)
     double a[],u[]; int n;
       a = array containing symmetric input matrix A
       u = array containing the n components of the eigenvector
           at exit (vector normalized to 1)
       n = dimension of system
      return: ev = eigenvalue of A with maximum absolute value
              HUGE -> convergence failure
*/

double evmax(double *a,double *u,int n)
{ double *p,*q,*qm,*r,*s,*t;
  double ev,evm,c,h; int kc;
  q=(double *)calloc(n,sizeof(double)); qm=q+n;
  *(qm-1)=1.; ev=0.;
  for(kc=0; kc<200 ;++kc){ h=c=0.; evm=ev;
    for(p=u,r=a,s=q; s<qm ;){ *p=0.;
      for(t=q; t<qm ;) *p+= *r++ * *t++;
      c+= *p * *p; h+= *p++ * *s++;
     }
    ev=c/h; c=sqrt(c);
    for(p=u,s=q; s<qm ;){ *p/=c; *s++ = *p++;}
    if(((c=ev-evm)<0.? -c:c)<1.e-16*(ev<0.? -ev:ev)){
      free(q); return ev; }
   }
  free(q); for(kc=0; kc<n ;) u[kc++]=0.;
  return 0.;
} 

/*
   Compute the Hermitian conjugate in place, A -> A^.

     void hconj(Cpx *a,int n)
       a = pointer to input array for the complex matrix A
           This is converted to the Hermitian conjugate A^.
       n = dimension (dim(a)=n*n)
*/
void hconj(Cpx *a,int n)
{ Cpx s,*p,*q;
  int i,j,e;
  for(i=0,e=n-1; i<n ;++i,--e,a+=n+1){
    for(j=0,p=a+1,q=a+n; j<e ;++j){
      s= *p; s.im= -s.im; p->re=q->re;
      (p++)->im= -q->im; *q=s; q+=n;
     }
    a->im= -a->im;
   }
}


/*
Compute the eigenvalues of a Hermitian matrix.

     void heigval(Cpx *a,double *ev,int n)
       a = pointer to array for the Hermitian matrix H
           These values are altered by the computation.
       ev = pointer to array that is loaded with the
            eigenvalues of H by the computation
       n = dimension (dim(a)=n*n, dim(ev)=n)
*/
void heigval(Cpx *a,double *ev,int n)
{ double *dp;
  dp=(double *)calloc(n,sizeof(double));
  chouse(a,ev,dp,n);
  qreval(ev,dp,n);
  free(dp);
}

/*
Compute the eigenvalues and eigenvectors of a Hermitian
     matrix.

     void heigvec(Cpx *a,double *ev,int n)
       a = pointer to array for the hermitian matrix H
           This array is loaded with a unitary matrix of
           eigenvectors E by the computation.
       ev = pointer to array that is loaded with the
            eigenvalues of H by the computation
       n = dimension (dim(a)=n*n, dim(ev)=n)

     The eigen vector matrix output E satisfies

          E^*E = I  and  A = E*D*E^

     where  D[i,j] = ev[i] for i=j and 0 otherwise
     and E^ is the Hermitian conjugate of E.
     The columns of E are the eigenvectors of A.
*/     
void heigvec(Cpx *a,double *ev,int n)
{ double *dp;
  dp=(double *)calloc(n,sizeof(double));
  chousv(a,ev,dp,n);
  qrecvc(ev,a,dp,n); hconj(a,n);
  free(dp);
}

/*

     Compute the eigenvalue of maximum absolute value and
     the corresponding eigenvector of a Hermitian matrix.

     double hevmax(Cpx *a,Cpx *u,int n)
     Cpx *a,*u; int n;
       a = pointer to array for the Hermitian matrix H
       u = pointer to array for the eigenvector umax
       n = dimension (dim(a)=n*n, dim(u)=n)
      return value: emax = eigenvalue of H with largest
                           absolute value

     The eigenvector u and eigenvalue emax are related by  u^*A*u = emax.

------------------------------------------------------------------------------
     Hermitian Eigensystem Auxiliaries:
------------------------------------------------------------------------------

     The following routines are called by the Hermitian eigensystem
     functions. They are not normally called by the user.
*/

double hevmax(Cpx *a,Cpx *u,int n)
{ Cpx *x,*p,h;
  double e,ep,s,t,te=1.e-12;
  int k,j;
  x=(Cpx *)calloc(n,sizeof(Cpx));
  x[0].re=1.; e=0.;
  do{
    for(k=0,p=a,s=t=0.; k<n ;++k){
      for(j=0,h.re=h.im=0.; j<n ;++j,++p){
        h.re+=p->re*x[j].re-p->im*x[j].im;
	h.im+=p->im*x[j].re+p->re*x[j].im;
       }
      s+=h.re*h.re+h.im*h.im;
      t+=h.re*x[k].re+h.im*x[k].im;
      u[k]=h;
     }
    ep=e; e=s/t; s=1./sqrt(s);
    for(k=0; k<n ;++k){
      u[k].re*=s; u[k].im*=s; x[k]=u[k];
     }
   } while(fabs(e-ep)>fabs(te*e));
  free(x);
  return e;
} 

/* Generate a Hermitian matrix with specified eigen values and
     eigenvectors.
  
     void hmgen(Cpx *h,double *ev,Cpx *u,int n)
       h = pointer to complex array of output matrix H
       ev = pointer to real array of input eigen values
       u = pointer to complex array of unitary matrix U
       n = dimension (dim(h)=dim(u)=n*n, dim(ev)=n)

     If D is a diagonal matrix with D[i,j] = ev[i] for i=j and 0
     otherwise.  H = U*D*U^. The columns of U are eigenvectors.

*/
void hmgen(Cpx *h,double *ev,Cpx *u,int n)
{ Cpx *v,*p;
  int i,j; double e;
  v=(Cpx *)calloc(n*n,sizeof(Cpx));
  cmcpy(v,u,n*n); hconj(v,n);
  for(i=0,p=v; i<n ;++i){
    for(j=0,e=ev[i]; j<n ;++j,++p){
      p->re*=e; p->im*=e;
     }
   }
  cmmul(h,u,v,n);
  free(v);
}

/*

     Transform a real symmetric matrix to tridiagonal form.

     void house(double *a,double *d,double *dp,int n)
       a = pointer to array of the symmetric input matrix A
           These values are altered by the computation.
       d = pointer to array of output diagonal elements
       dp = pointer to array of n-1 elements neighboring the
            diagonal in the symmetric transformed matrix
       n = dimension (dim(a)= n*n, dim(d)=dim(dp)=n)

     The output arrays are related to the tridiagonal matrix T by

          T[i,i+1] = T[i+1,i] = dp[i] for i=0 to n-2, and
          T[i,i] = d[i] for i=0 to n-1.
*/

void house(double *a,double *d,double *dp,int n)
{ double sc,x,y,h;
  int i,j,k,m,e;
  double *qw,*qs,*pc,*p;
  qs=(double *)calloc(2*n,sizeof(double));
  for(j=0,qw=qs+n,pc=a; j<n ;pc+=n+1) qw[j++]= *pc;
  for(j=0,pc=a; j<n-2 ;++j,pc+=n+1){
    m=n-j-1;
    for(i=1,sc=0.; i<=m ;++i) sc+=pc[i]*pc[i];
    if(sc>0.){ sc=sqrt(sc);
      if((x= *(pc+1))<0.){ y=x-sc; h=1./sqrt(-2.*sc*y);}
      else{ y=x+sc; h=1./sqrt(2.*sc*y); sc= -sc;}
      for(i=0,qw=pc+1; i<m ;++i){
        qs[i]=0.; if(i) qw[i]*=h; else qw[i]=y*h;
       }
      for(i=0,e=j+2,p=pc+n+1,h=0.; i<m ;++i,p+=e++){
        qs[i]+=(y=qw[i])* *p++;
	for(k=i+1; k<m ;++k){
          qs[i]+=qw[k]* *p; qs[k]+=y* *p++;
         }
        h+=y*qs[i];
       }
      for(i=0; i<m ;++i){
	qs[i]-=h*qw[i]; qs[i]+=qs[i];
       }
      for(i=0,e=j+2,p=pc+n+1; i<m ;++i,p+=e++){
        for(k=i; k<m ;++k) *p++ -=qw[i]*qs[k]+qs[i]*qw[k];
       }
     }
    d[j]= *pc; dp[j]=sc;
   }
  d[j]= *pc; dp[j]= *(pc+1); d[j+1]= *(pc+n+1);
  for(j=0,pc=a,qw=qs+n; j<n ;++j,pc+=n+1){
    *pc=qw[j];
    for(i=1,p=pc+n; i<n-j ;p+=n) pc[i++]= *p;
   }
  free(qs);
}



/*
Print an array in n rows of m columns to stdout.

     void matprt(double *a,int n,int m,char *fmt)
       a = pointer to input array stored in row order (size = n*m)
       n = number of output rows
       m = number of output columns
       fmt= pointer to character array containing format string
             (printf formats eg. " %f")

     Long rows may overflow the print line.
*/
void matprt(double *a,int n,int m,char *fmt)
{ int i,j; double *p;
  for(i=0,p=a; i<n ;++i){
    for(j=0; j<m ;++j) printf(fmt,*p++);
    printf("\n");
   }
}

/*
Transpose an m by n matrix A = B~.

     void mattr(double *a,double *b,int m,int n)
       a = pointer to array containing output n by m matrix 
       b = pointer to array containing input m by n matrix
            (matrices stored in row order)
       m,n = dimension parameters (dim(a)=dim(b)=n*m)
*/
void mattr(double *a,double *b,int m,int n)
{ double *p; int i,j;
  for(i=0; i<n ;++i,++b)
    for(j=0,p=b; j<m ;++j,p+=n) *a++ = *p;
}


/*
     Print formatted array output to a file.

     void fmatprt(FILE *fp,double *a,int n,int m,char *fmt)
       fp = pointer to file opened for writing
       a = pointer to input array stored in row order (size = n*m)
       n = number of output rows
       m = number of output columns
       fmt= pounter to character array containing format string
             (printf formats eg. " %f")

*/
void fmatprt(FILE *fp,double *a,int n,int m,char *fmt)
{ int i,j; double *p;
  for(i=0,p=a; i<n ;++i){
    for(j=0; j<m ;++j) fprintf(fp,fmt,*p++);
    fprintf(fp,"\n");
   }
}


/*
Copy an array a = b.

     void mcopy(double *a,double *b,int n)
       a = array containing output values, identical to input
           b at exit
       b = input array
       n = dimension of arrays

*/
void mcopy(double *a,double *b,int m)
{ double *p,*q; int k;
  for(p=a,q=b,k=0; k<m ;++k) *p++ = *q++;
}


/*
Invert (in place) a general real matrix A -> Inv(A).

     int minv(double a[],int n)
       a = array containing the input matrix A
           This is converted to the inverse matrix.
       n = dimension of the system (i.e. A is n x n )
      return: 0 -> normal exit
              1 -> singular input matrix
*/

int minv(double *a,int n)
{ int lc,*le; double s,t,tq=0.,zr=1.e-15;
  double *pa,*pd,*ps,*p,*q,*q0;
  int i,j,k,m;
  le=(int *)malloc(n*sizeof(int));
  q0=(double *)malloc(n*sizeof(double));
  for(j=0,pa=pd=a; j<n ;++j,++pa,pd+=n+1){
    if(j>0){
      for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *q++ = *p;
      for(i=1; i<n ;++i){ lc=i<j?i:j;
        for(k=0,p=pa+i*n-j,q=q0,t=0.; k<lc ;++k) t+= *p++ * *q++;
      	q0[i]-=t;
       }
      for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *p= *q++;
     }
    s=fabs(*pd); lc=j;
    for(k=j+1,ps=pd; k<n ;++k){
      if((t=fabs(*(ps+=n)))>s){ s=t; lc=k;}
     }
    tq=tq>s?tq:s; if(s<zr*tq){ free(le-j); free(q0); return -1;}
    *le++ =lc;
    if(lc!=j){
      for(k=0,p=a+n*j,q=a+n*lc; k<n ;++k){
        t= *p; *p++ = *q; *q++ =t;
       }
     }
    for(k=j+1,ps=pd,t=1./ *pd; k<n ;++k) *(ps+=n)*=t;
    *pd=t;
   }
  for(j=1,pd=ps=a; j<n ;++j){
    for(k=0,pd+=n+1,q= ++ps; k<j ;++k,q+=n) *q*= *pd;
   }
  for(j=1,pa=a; j<n ;++j){ ++pa;
    for(i=0,q=q0,p=pa; i<j ;++i,p+=n) *q++ = *p;
    for(k=0; k<j ;++k){ t=0.;
      for(i=k,p=pa+k*n+k-j,q=q0+k; i<j ;++i) t-= *p++ * *q++;
      q0[k]=t;
     }
    for(i=0,q=q0,p=pa; i<j ;++i,p+=n) *p= *q++;
   }
  for(j=n-2,pd=pa=a+n*n-1; j>=0 ;--j){ --pa; pd-=n+1;
    for(i=0,m=n-j-1,q=q0,p=pd+n; i<m ;++i,p+=n) *q++ = *p;
    for(k=n-1,ps=pa; k>j ;--k,ps-=n){ t= -(*ps);
      for(i=j+1,p=ps,q=q0; i<k ;++i) t-= *++p * *q++;
      q0[--m]=t;
     }
    for(i=0,m=n-j-1,q=q0,p=pd+n; i<m ;++i,p+=n) *p= *q++;
   }
  for(k=0,pa=a; k<n-1 ;++k,++pa){
    for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *q++ = *p;
    for(j=0,ps=a; j<n ;++j,ps+=n){
      if(j>k){ t=0.; p=ps+j; i=j;}
      else{ t=q0[j]; p=ps+k+1; i=k+1;}
      for(; i<n ;) t+= *p++ *q0[i++];
      q0[j]=t;
     }
    for(i=0,q=q0,p=pa; i<n ;++i,p+=n) *p= *q++;
   }
  for(j=n-2,le--; j>=0 ;--j){
    for(k=0,p=a+j,q=a+ *(--le); k<n ;++k,p+=n,q+=n){
      t=*p; *p=*q; *q=t;
     }
   }
  free(le); free(q0);
  return 0;
}


/*
   Multiply two real square matrices C = A * B.

     void mmul(double *c,double *a,double *b,int n)
     double *a,*b,*c; int n;
       a = pointer to store for left product matrix
       b = pointer to store for right product matrix
       c = pointer to store for output matrix
       n = dimension (dim(a)=dim(b)=dim(c)=n*n)
*/

void mmul(double *c,double *a,double *b,int n)
{ double *p,*q,s; int i,j,k;
  trnm(b,n);
  for(i=0; i<n ;++i,a+=n){
    for(j=0,q=b; j<n ;++j){
      for(k=0,p=a,s=0.; k<n ;++k) s+= *p++ * *q++;
      *c++ =s;
     }
   }
  trnm(b,n);
}

/*
Generate a general orthogonal transformation matrix, E~*E = I.

     void ortho(double *e,int n)
       e = pointer to array of orthogonal output matrix E
       n = dimension of vector space (dim(e)=n*n)

     This function calls on the uniform random generator 'unfl' to
     produce random rotation angles. Therefore this random generator
     should be initialized by a call of 'setunfl' before calling
     ortho (see Chapter 7).
*/

static double tpi=6.28318530717958647;
void ortho(double *e,int n)
{ int i,j,k,m;
  double *p,*q,c,s,a;
  for(i=0,p=e; i<n ;++i){
    for(j=0; j<n ;++j){
      if(i==j) *p++ =1.; else *p++ =0.;
     }
   }
  for(i=0,m=n-1; i<m ;++i){
    for(j=i+1; j<n ;++j){
      a=tpi*unfl();
      c=cos(a); s=sin(a);
      p=e+n*i; q=e+n*j;
      for(k=0; k<n ;++k){
        a=*p*c+ *q*s; *q=*q*c- *p*s;
        *p++ =a; ++q;
       }
     }
   }
}


/*
Perform an orthogonal similarity transform C = A*B*A~.

     void otrma(double *c,double *a,double *b,int n)
       c = pointer to array of output matrix C
       a = pointer to array of transformation A
       b = pointer to array of input matrix B
       n = dimension (dim(a)=dim(b)=dim(c)=n*n)
*/
void otrma(double *c,double *a,double *b,int n)
{ double z,*q0,*p,*s,*t;
  int i,j,k;
  q0=(double *)calloc(n,sizeof(double));
  for(i=0; i<n ;++i,++c){
    for(j=0,t=b; j<n ;++j){
      for(k=0,s=a+i*n,z=0.; k<n ;++k) z+= *t++ * *s++;
      q0[j]=z;
     }
    for(j=0,p=c,t=a; j<n ;++j,p+=n){
      for(k=0,s=q0,z=0.; k<n ;++k) z+= *t++ * *s++;
      *p=z;
     }
   }
  free(q0);
}

/*
     Perform a similarity transform on a symmetric matrix S = A*B*A~.

     void otrsm(double *sm,double *a,double *b,int n)
       sm = pointer to array of output matrix S
       a = pointer to array of transformation matrix A
       b = pointer to array of symmetric input matrix B
       n = dimension (dim(a)=dim(b)=dim(sm)=n*n)
*/
void otrsm(double *sm,double *a,double *b,int n)
{ double z,*q0,*p,*s,*t;
  int i,j,k;
  q0=(double *)calloc(n,sizeof(double));
  for(i=0; i<n ;++i){
    for(j=0,t=b; j<n ;++j){
      for(k=0,s=a+i*n,z=0.; k<n ;++k) z+= *t++ * *s++;
      q0[j]=z;
     }
    for(j=0,p=sm+i,t=a; j<=i ;++j,p+=n){
      for(k=0,s=q0,z=0.; k<n ;++k) z+= *t++ * *s++;
      *p=z; if(j<i) sm[i*n+j]=z;
     }
   }
  free(q0);
}

/*

     Invert (in place) a symmetric real matrix, V -> Inv(V).

     int psinv(double v[],int n)
       v = array containing a symmetric input matrix
           This is converted to the inverse matrix.
       n = dimension of the system (dim(v)=n*n)
      return: 0 -> normal exit
              1 -> input matrix not positive definite

           The input matrix V is symmetric (V[i,j] = V[j,i]).
*/
int psinv(double *v,int n)
{ double z,*p,*q,*r,*s,*t; int j,k;
  for(j=0,p=v; j<n ;++j,p+=n+1){
    for(q=v+j*n; q<p ;++q) *p-= *q* *q;
    if(*p<=0.) return -1;
    *p=sqrt(*p);
    for(k=j+1,q=p+n; k<n ;++k,q+=n){
      for(r=v+j*n,s=v+k*n,z=0.; r<p ;) z+= *r++ * *s++;
      *q-=z; *q/= *p;
     }
   }
  trnm(v,n);
  for(j=0,p=v; j<n ;++j,p+=n+1){ *p=1./ *p;
    for(q=v+j,t=v; q<p ;t+=n+1,q+=n){
      for(s=q,r=t,z=0.; s<p ;s+=n) z-= *s * *r++;
      *q=z* *p; }
   }
  for(j=0,p=v; j<n ;++j,p+=n+1){
    for(q=v+j,t=p-j; q<=p ;q+=n){
      for(k=j,r=p,s=q,z=0.; k<n ;++k) z += *r++ * *s++;
      *t++ =(*q=z); }
   }
  return 0;
}


/*
Perform a QR reduction of a bidiagonal matrix.

     int qrbdi(double *d,double *e,int m)
       d = pointer to n-dimensional array of diagonal values
           (overloaded by diagonal elements of reduced matrix)
       e = pointer to store of superdiagonal values (loaded in
           first m-1 elements of the array). Values are altered
           by the computation.
       m = dimension of the d and e arrays
      return value: N = number of QR iterations required
*/
int qrbdi(double *dm,double *em,int m)
{ int i,j,k,n;
  double u,x,y,a,b,c,s,t;
  for(j=1,t=fabs(dm[0]); j<m ;++j)
    if((s=fabs(dm[j])+fabs(em[j-1]))>t) t=s;
  t*=1.e-15; n=100*m;
  for(j=0; m>1 && j<n ;++j){
    for(k=m-1; k>0 ;--k){
      if(fabs(em[k-1])<t) break;
      if(fabs(dm[k-1])<t){
        for(i=k,s=1.,c=0.; i<m ;++i){
          a=s*em[i-1]; b=dm[i]; em[i-1]*=c;
          dm[i]=u=sqrt(a*a+b*b); s= -a/u; c=b/u;
         }
        break;
       }
     }
    y=dm[k]; x=dm[m-1]; u=em[m-2];
    a=(y+x)*(y-x)-u*u; s=y*em[k]; b=s+s;
    u=sqrt(a*a+b*b);
	if(u>0.){
      c=sqrt((u+a)/(u+u));
	  if(c!=0.) s/=(c*u); else s=1.;
	  for(i=k; i<m-1 ;++i){
        b=em[i];
        if(i>k){
          a=s*em[i]; b*=c;
          em[i-1]=u=sqrt(x*x+a*a);
          c=x/u; s=a/u;
         }
        a=c*y+s*b; b=c*b-s*y;
        s*=dm[i+1]; dm[i]=u=sqrt(a*a+s*s);
        y=c*dm[i+1]; c=a/u; s/=u;
        x=c*b+s*y; y=c*y-s*b;
	   }
     }
    em[m-2]=x; dm[m-1]=y;
    if(fabs(x)<t) --m;
    if(m==k+1) --m;
   }
  return j;
}

/*
Use QR transformations to reduce a real symmetric tridiagonal
     matrix to diagonal form, and update a unitary transformation
     matrix.

     void qrecvc(double *ev,Cpx *evec,double *dp,int n)
       ev = pointer to input array of diagonal elements
            The computation transforms these to eigenvalues
            of the input matrix.
       evec = pointer to input array of a unitary transformation
              matrix U. The computation applies the QR rotations
              to this matrix.
       dp = pointer to input array of elements neighboring the
            diagonal. These values are altered by the computation.
       n = dimension parameter (dim(ev)=dim(dp)=n, dim(evec)=n*n)

     This function operates on the output of 'chousv'.
*/

void qrecvc(double *ev,Cpx *evec,double *dp,int n)
{ double cc,sc,d,x,y,h,tzr=1.e-15;
  int i,j,k,m,nqr=50*n;
  Cpx *p;
  for(j=0,m=n-1;j<nqr;++j){
    while(1){
	  if(m<1) break;
	  k=m-1;
      if(fabs(dp[k])<=fabs(ev[m])*tzr) --m;
      else{ x=(ev[k]-ev[m])/2.; h=sqrt(x*x+dp[k]*dp[k]);
        if(m>1 && fabs(dp[m-2])>fabs(ev[k])*tzr) break;
	    if((cc=sqrt((1.+x/h)/2.))!=0.) sc=dp[k]/(2.*cc*h); else sc=1.;
        x+=ev[m]; ev[m--]=x-h; ev[m--]=x+h;
        for(i=0,p=evec+n*(m+1); i<n ;++i,++p){
	      h=p[0].re; p[0].re=cc*h+sc*p[n].re;
	      p[n].re=cc*p[n].re-sc*h;
	      h=p[0].im; p[0].im=cc*h+sc*p[n].im;
	      p[n].im=cc*p[n].im-sc*h;
         }
       }
     }
    if(x>0.) d=ev[m]+x-h; else d=ev[m]+x+h;
    cc=1.; y=0.; ev[0]-=d;
    for(k=0; k<m ;++k){
      x=ev[k]*cc-y; y=dp[k]*cc; h=sqrt(x*x+dp[k]*dp[k]);
      if(k>0) dp[k-1]=sc*h;
      ev[k]=cc*h; cc=x/h; sc=dp[k]/h; ev[k+1]-=d; y*=sc;
      ev[k]=cc*(ev[k]+y)+ev[k+1]*sc*sc+d;
      for(i=0,p=evec+n*k; i<n ;++i,++p){
        h=p[0].re; p[0].re=cc*h+sc*p[n].re;
	    p[n].re=cc*p[n].re-sc*h;
	    h=p[0].im; p[0].im=cc*h+sc*p[n].im;
	    p[n].im=cc*p[n].im-sc*h;
       }
     }
    ev[k]=ev[k]*cc-y; dp[k-1]=ev[k]*sc; ev[k]=ev[k]*cc+d;
   }
}


/*
Perform a QR reduction of a real symmetric tridiagonal
     matrix to diagonal form.

     int qreval(double *ev,double *dp,int n)
       ev = pointer to array of input diagonal elements
            The computation overloads this array with
            eigenvalues.
       dp = pointer to array input elements neighboring the
            diagonal. This array is altered by the computation.
       n = dimension (dim(ev)=dim(dp)= n)
*/
int qreval(double *ev,double *dp,int n)
{ double cc,sc,d,x,y,h,tzr=1.e-15;
  int j,k,m,mqr=8*n;
  for(j=0,m=n-1;;++j){
    while(1){ if(m<1) return 0; k=m-1;
      if(fabs(dp[k])<=fabs(ev[m])*tzr) --m;
      else{ x=(ev[k]-ev[m])/2.; h=sqrt(x*x+dp[k]*dp[k]);
        if(m>1 && fabs(dp[m-2])>fabs(ev[k])*tzr) break;
        x+=ev[m]; ev[m--]=x-h; ev[m--]=x+h;
       }
     }
    if(j>mqr) return -1;
    if(x>0.) d=ev[m]+x-h; else d=ev[m]+x+h;
    cc=1.; y=0.; ev[0]-=d;
    for(k=0; k<m ;++k){
      x=ev[k]*cc-y; y=dp[k]*cc; h=sqrt(x*x+dp[k]*dp[k]);
      if(k>0) dp[k-1]=sc*h;
      ev[k]=cc*h; cc=x/h; sc=dp[k]/h; ev[k+1]-=d; y*=sc;
      ev[k]=cc*(ev[k]+y)+ev[k+1]*sc*sc+d;
     }
    ev[k]=ev[k]*cc-y; dp[k-1]=ev[k]*sc; ev[k]=ev[k]*cc+d;
   }
  return 0;
}


/*
     Perform a QR reduction of a real symmetric tridiagonal matrix
     to diagonal form and update an orthogonal transformation matrix.

     int qrevec(double *ev,double *evec,double *dp,int n)
       ev = pointer to array of input diagonal elements
            that the computation overloads with eigenvalues
       evec = pointer array of orthogonal input matrix
              This is updated by the computation to a matrix
              of eigenvectors.
       dp = pointer to array input elements neighboring the
            diagonal. This array is altered by the computation.
       n = dimension (dim(ev)=dim(dp)= n)

     This function operates on the output of 'housev'.

*/
int qrevec(double *ev,double *evec,double *dp,int n)
{ double cc,sc,d,x,y,h,tzr=1.e-15;
  int i,j,k,m,mqr=8*n;
  double *p;
  for(j=0,m=n-1;;++j){
    while(1){ if(m<1) return 0; k=m-1;
      if(fabs(dp[k])<=fabs(ev[m])*tzr) --m;
      else{ x=(ev[k]-ev[m])/2.; h=sqrt(x*x+dp[k]*dp[k]);
        if(m>1 && fabs(dp[m-2])>fabs(ev[k])*tzr) break;
	    if((cc=sqrt((1.+x/h)/2.))!=0.) sc=dp[k]/(2.*cc*h); else sc=1.;
        x+=ev[m]; ev[m--]=x-h; ev[m--]=x+h;
        for(i=0,p=evec+n*(m+1); i<n ;++i,++p){
	      h=p[0]; p[0]=cc*h+sc*p[n]; p[n]=cc*p[n]-sc*h;
         }
       }
     }
    if(j>mqr) return -1;
    if(x>0.) d=ev[m]+x-h; else d=ev[m]+x+h;
    cc=1.; y=0.; ev[0]-=d;
    for(k=0; k<m ;++k){
      x=ev[k]*cc-y; y=dp[k]*cc; h=sqrt(x*x+dp[k]*dp[k]);
      if(k>0) dp[k-1]=sc*h;
      ev[k]=cc*h; cc=x/h; sc=dp[k]/h; ev[k+1]-=d; y*=sc;
      ev[k]=cc*(ev[k]+y)+ev[k+1]*sc*sc+d;
      for(i=0,p=evec+n*k; i<n ;++i,++p){
        h=p[0]; p[0]=cc*h+sc*p[n]; p[n]=cc*p[n]-sc*h;
       }
     }
    ev[k]=ev[k]*cc-y; dp[k-1]=ev[k]*sc; ev[k]=ev[k]*cc+d;
   }
  return 0;
}

/*
Multiply two matrices Mat = A*B.

     void rmmult(double *mat,double *a,double *b,int m,int k,int n)
     double mat[],a[],b[]; int m,k,n;
       mat = array containing m by n product matrix at exit
       a = input array containing m by k matrix
       b = input array containing k by n matrix
            (all matrices stored in row order)
       m,k,n = dimension parameters of arrays
*/
void rmmult(double *rm,double *a,double *b,int n,int m,int l)
{ double z,*q0,*p,*q; int i,j,k;
  q0=(double *)calloc(m,sizeof(double));
  for(i=0; i<l ;++i,++rm){
    for(k=0,p=b+i; k<m ;p+=l) q0[k++]= *p;
    for(j=0,p=a,q=rm; j<n ;++j,q+=l){
      for(k=0,z=0.; k<m ;) z+= *p++ * q0[k++];
      *q=z;
     }
   }
  free(q0);
}

/*
   Invert an upper right triangular matrix T -> Inv(T).

     int ruinv(double *a,int n)
       a = pointer to array of upper right triangular matrix
           This is replaced by the inverse matrix.
       n = dimension (dim(a)=n*n)
      return value: status flag, with 0 -> matrix inverted
                                     -1 -> matrix singular

*/

int ruinv(double *a,int n)
{ int j;  //double fabs();
  double tt,z,*p,*q,*r,*s,*t;
  for(j=0,tt=0.,p=a; j<n ;++j,p+=n+1) if((z=fabs(*p))>tt) tt=z;
  tt*=1.e-16;
  for(j=0,p=a; j<n ;++j,p+=n+1){
    if(fabs(*p)<tt) return -1;
    *p=1./ *p;
    for(q=a+j,t=a; q<p ;t+=n+1,q+=n){
      for(s=q,r=t,z=0.; s<p ;s+=n) z-= *s * *r++;
      *q=z* *p;
     }
   }
  return 0;
}

/*
   Construct a symmetric matrix from specified eigenvalues and
     eigenvectors.

     void smgen(double *a,double *eval,double *evec,int n)
       a = pointer to array containing output matrix
       eval = pointer to array containing the n eigenvalues
       evec = pointer to array containing eigenvectors
              (n by n with kth column the vector corresponding
               to the kth eigenvalue)
       n = system dimension
           
          If D is the diagonal matrix of eigenvalues
          and  E[i,j] = evec[j+n*i] , then   A = E*D*E~.
*/
void smgen(double *a,double *eval,double *evec,int n)
{ double *p,*q,*ps,*r,*s,*t,*v=evec+n*n;
  for(ps=a,p=evec; p<v ;p+=n){
    for(q=evec; q<v ;q+=n,++ps){ *ps=0.;
      for(r=eval,s=p,t=q; r<eval+n ;)
        *ps+= *r++ * *s++ * *t++;
     }
   }
}

/*
Solve a symmetric positive definite linear system S*x = b.

     int solvps(double a[],double b[],int n)
       a = array containing system matrix S (altered to
            Cholesky upper right factor by computation)
       b = array containing system vector b as input and
           solution vector x as output
       n = dimension of system
      return: 0 -> normal exit
              1 -> input matrix not positive definite
*/

int solvps(double *a,double *b,int n)
{ double *p,*q,*r,*s,t;
  int j,k;
  for(j=0,p=a; j<n ;++j,p+=n+1){
    for(q=a+j*n; q<p ;++q) *p-= *q* *q;
    if(*p<=0.) return -1;
    *p=sqrt(*p);
    for(k=j+1,q=p+n; k<n ;++k,q+=n){
      for(r=a+j*n,s=a+k*n,t=0.; r<p ;) t+= *r++ * *s++;
      *q-=t; *q/= *p;
     }
   }
  for(j=0,p=a; j<n ;++j,p+=n+1){
    for(k=0,q=a+j*n; k<j ;) b[j]-=b[k++]* *q++;
    b[j]/= *p;
   }
  for(j=n-1,p=a+n*n-1; j>=0 ;--j,p-=n+1){
    for(k=j+1,q=p+n; k<n ;q+=n) b[j]-=b[k++]* *q;
    b[j]/= *p;
   }
  return 0;
}

/*

     Solve an upper right triangular linear system T*x = b.

     int solvru(double *a,double *b,int n)
       a = pointer to array of upper right triangular matrix T
       b = pointer to array of system vector
           The computation overloads this with the
           solution vector x.
       n = dimension (dim(a)=n*n,dim(b)=n)
      return value: f = status flag, with 0 -> normal exit
                                         -1 -> system singular
*/
int solvru(double *a,double *b,int n)
{ int j,k; // double fabs();
  double s,t,*p,*q;
  for(j=0,s=0.,p=a; j<n ;++j,p+=n+1) if((t=fabs(*p))>s) s=t;
  s*=1.e-16;
  for(j=n-1,p=a+n*n-1; j>=0 ;--j,p-=n+1){
    for(k=j+1,q=p+1; k<n ;) b[j]-=b[k++]* *q++;
    if(fabs(*p)<s) return -1;
    b[j]/= *p;
   }
  return 0;
}

/*
     Solve a tridiagonal linear system M*x = y.

     void solvtd(double a[],double b[],double c[],double x[],int m)
       a = array containing m+1 diagonal elements of M
       b = array of m elements below the main diagonal of M
       c = array of m elements above the main diagonal
       x = array containing the system vector y initially, and
           the solution vector at exit (m+1 elements)
       m = dimension parameter ( M is (m+1)x(m+1) )
*/
void solvtd(double *a,double *b,double *c,double *x,int m)
{ double s; int j;
  for(j=0; j<m ;++j){ s=b[j]/a[j];
    a[j+1]-=s*c[j]; x[j+1]-=s*x[j];}
  for(j=m,s=0.; j>=0 ;--j){
    x[j]-=s*c[j]; s=(x[j]/=a[j]);}
}


/*
Compute the singular value transformation with partial U
     matrix U1 efficiently for m >> n.

     #include <math.h>
     int sv2u1v(d,a,m,v,n)
     double *d,*a,*v; int m,n;
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (At output a is overloaded by the matrix U1
            whose n columns are orthogonal vectors equal to
            the first n columns of U.)
       v = pointer to store for n by n orthogonal matrix V
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/
int sv2u1v(double *d,double *a,int m,double *v,int n)
{ double *p,*p1,*q,*pp,*w,*e;
  double s,t,h,r,sv;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m+n,sizeof(double)); e=w+m;
  for(i=0,mm=m,p=a; i<n ;++i,--mm,p+=n+1){
    if(mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r=r*s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
	for(j=1,q=p; j<mm ;) *(q+=n)=w[j++]*t;
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
   }
  for(i=0,q=v,p=a; i<n ;++i){
    for(j=0; j<n ;++j,++q,++p){
      if(j<i) *q=0.;
      else if(j==i) *q=d[i];
      else *q= *p;
     }
   }
  atou1(a,m,n);
  for(i=0,mm=n,nm=n-1,p=v; i<n ;++i,--mm,--nm,p+=n+1){
    if(i && mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  for(j=0,q=p+k,r*=s; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(k=0,p1=a+i; k<m ;++k,p1+=n){
          for(j=0,q=p1,r=0.; j<mm ;) r+=w[j++]* *q++;
	  for(j=0,q=p1,r*=s; j<mm ;) *q++ -=r*w[j++];
	 }
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
    p1=p+1;
    if(nm>1){ sv=h=0.;
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
        sv=1.+fabs(*p1/h);
	s+= *p1*h; s=1./s; t=1./(*p1+=h);
	for(k=n,ms=n*(n-i); k<ms ;k+=n){
	  for(j=0,q=p1,pp=p1+k,r=0.; j<nm ;++j) r+= *q++ * *pp++;
	  for(j=0,q=p1,pp=p1+k,r*=s; j<nm ;++j) *pp++ -=r* *q++;
	 }
	for(j=1,q=p1+1; j<nm ;++j) *q++ *=t;
       }
      *p1=sv; e[i]= -h;
     }
    if(nm==1) e[i]= *p1;
   }
  atovm(v,n);
  qrbdu1(d,e,a,m,v,n);
  for(i=0; i<n ;++i){
    if(d[i]<0.){ d[i]= -d[i];
      for(j=0,p=v+i; j<n ;++j,p+=n) *p= - *p;
     }
   }
  free(w);
  return 0;
} 


int qrbdu1(double *dm,double *em,double *um,int mm,double *vm,int m)
{ int i,j,k,n,jj,nm;
  double u,x,y,a,b,c,s,t,w,*p,*q;
  for(j=1,t=fabs(dm[0]); j<m ;++j)
    if((s=fabs(dm[j])+fabs(em[j-1]))>t) t=s;
  t*=1.e-15; n=100*m; nm=m;
  for(j=0; m>1 && j<n ;++j){
    for(k=m-1; k>0 ;--k){
      if(fabs(em[k-1])<t) break;
      if(fabs(dm[k-1])<t){
        for(i=k,s=1.,c=0.; i<m ;++i){
          a=s*em[i-1]; b=dm[i]; em[i-1]*=c;
          dm[i]=u=sqrt(a*a+b*b); s= -a/u; c=b/u;
          for(jj=0,p=um+k-1; jj<mm ;++jj,p+=nm){
            q=p+i-k+1;
            w=c* *p+s* *q; *q=c* *q-s* *p; *p=w;
           }
         }
        break;
       }
     }
    y=dm[k]; x=dm[m-1]; u=em[m-2];
    a=(y+x)*(y-x)-u*u; s=y*em[k]; b=s+s;
    u=sqrt(a*a+b*b);
	if(u>0.){
      c=sqrt((u+a)/(u+u));
	  if(c!=0.) s/=(c*u); else s=1.;
      for(i=k; i<m-1 ;++i){
        b=em[i];
        if(i>k){
          a=s*em[i]; b*=c;
          em[i-1]=u=sqrt(x*x+a*a);
          c=x/u; s=a/u;
         }
        a=c*y+s*b; b=c*b-s*y;
        for(jj=0,p=vm+i; jj<nm ;++jj,p+=nm){
          w=c* *p+s* *(p+1); *(p+1)=c* *(p+1)-s* *p; *p=w;
         }
        s*=dm[i+1]; dm[i]=u=sqrt(a*a+s*s);
        y=c*dm[i+1]; c=a/u; s/=u;
        x=c*b+s*y; y=c*y-s*b;
        for(jj=0,p=um+i; jj<mm ;++jj,p+=nm){
          w=c* *p+s* *(p+1); *(p+1)=c* *(p+1)-s* *p; *p=w;
         }
	   }
     }
    em[m-2]=x; dm[m-1]=y;
    if(fabs(x)<t) --m;
    if(m==k+1) --m; 
   }
  return j;
}

/*
Compute the singular value transformation when m >> n.

     int sv2uv(double *d,double *a,double *u,int m,double *v,int n)
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (A is altered by the computation)
       u = pointer to store for m by m orthogonal matrix U
       v = pointer to store for n by n orthogonal matrix V
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/
int sv2uv(double *d,double *a,double *u,int m,double *v,int n)
{ double *p,*p1,*q,*pp,*w,*e;
  double s,t,h,r,sv;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m+n,sizeof(double)); e=w+m;
  for(i=0,mm=m,p=a; i<n ;++i,--mm,p+=n+1){
    if(mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r=r*s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
	for(j=1,q=p; j<mm ;) *(q+=n)=w[j++]*t;
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
   }
  ldumat(a,u,m,n);
  for(i=0,q=a; i<n ;++i){
    for(j=0; j<n ;++j,++q){
      if(j<i) *q=0.;
      else if(j==i) *q=d[i];
     }
   }
  for(i=0,mm=n,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(i && mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  for(j=0,q=p+k,r*=s; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(k=0,p1=u+i; k<m ;++k,p1+=m){
          for(j=0,q=p1,r=0.; j<mm ;) r+=w[j++]* *q++;
	  for(j=0,q=p1,r*=s; j<mm ;) *q++ -=r*w[j++];
	 }
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
    p1=p+1;
    if(nm>1){ sv=h=0.;
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
        sv=1.+fabs(*p1/h);
	s+= *p1*h; s=1./s; t=1./(*p1+=h);
	for(k=n,ms=n*(n-i); k<ms ;k+=n){
	  for(j=0,q=p1,pp=p1+k,r=0.; j<nm ;++j) r+= *q++ * *pp++;
	  for(j=0,q=p1,pp=p1+k,r*=s; j<nm ;++j) *pp++ -=r* *q++;
	 }
	for(j=1,q=p1+1; j<nm ;++j) *q++ *=t;
       }
      *p1=sv; e[i]= -h;
     }
    if(nm==1) e[i]= *p1;
   }
  ldvmat(a,v,n);
  qrbdv(d,e,u,m,v,n);
  for(i=0; i<n ;++i){
    if(d[i]<0.){ d[i]= -d[i];
      for(j=0,p=v+i; j<n ;++j,p+=n) *p= - *p;
     }
   }
  free(w);
  return 0;
}



/*
     Compute singular values when m >> n.                         

     int sv2val(double *d,double *a,int m,int n)
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (A is altered by the computation)
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/
int sv2val(double *d,double *a,int m,int n)
{ double *p,*p1,*q,*w,*v;
  double s,h,u;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m,sizeof(double));
  for(i=0,mm=m,p=a; i<n && mm>1 ;++i,--mm,p+=n+1){
    for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
      w[j]= *q; s+= *q* *q;
     }
    if(s>0.){
      h=sqrt(s); if(*p<0.) h= -h;
      s+= *p*h; s=1./s; w[0]+=h;
      for(k=1,ms=n-i; k<ms ;++k){
	for(j=0,q=p+k,u=0.; j<mm ;q+=n) u+=w[j++]* *q;
	u=u*s;
	for(j=0,q=p+k; j<mm ;q+=n) *q-=u*w[j++];
       }
      *p= -h;
     }
   }
  for(i=0,p=a; i<n ;++i,p+=n){
    for(j=0,q=p; j<i ;++j) *q++ =0.;
   }
  for(i=0,mm=n,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(i && mm>1){
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; w[0]+=h;
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,u=0.; j<mm ;q+=n) u+=w[j++]* *q;
	  u*=s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=u*w[j++];
	 }
	*p= -h;
       }
     }
    p1=p+1;
    if(nm>1){
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
	s+= *p1*h; s=1./s; *p1+=h;
	for(k=n,ms=n*(m-i); k<ms ;k+=n){
	  for(j=0,q=p1,v=p1+k,u=0.; j<nm ;++j) u+= *q++ * *v++;
	  u*=s;
	  for(j=0,q=p1,v=p1+k; j<nm ;++j) *v++ -=u* *q++;
	 }
	*p1= -h;
       }
     }
   }
  for(j=0,p=a; j<n ;++j,p+=n+1){
    d[j]= *p; if(j<n-1) w[j]= *(p+1); else w[j]=0.;
   }
  qrbdi(d,w,n);
  for(i=0; i<n ;++i) if(d[i]<0.) d[i]= -d[i];
  free(w);
  return 0;
}


/*
Compute the singular value transformation with A overloaded by
     the partial U-matrix.

     int svdu1v(double *d,double *a,int m,double *v,int n)
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (At output a is overloaded by the matrix U1
            whose n columns are orthogonal vectors equal to
            the first n columns of U.)
       v = pointer to store for n by n orthogonal matrix V
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/
int svdu1v(double *d,double *a,int m,double *v,int n)
{ double *p,*p1,*q,*pp,*w,*e;
  double s,h,r,t,sv;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m+n,sizeof(double)); e=w+m;
  for(i=0,mm=m,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r*=s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(j=1,q=p; j<mm ;) *(q+=n)=t*w[j++];
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
    p1=p+1; sv=h=0.;
    if(nm>1){
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
        sv=1.+fabs(*p1/h);
	s+= *p1*h; s=1./s; t=1./(*p1+=h);
	for(k=n,ms=n*(m-i); k<ms ;k+=n){
	  for(j=0,q=p1,pp=p1+k,r=0.; j<nm ;++j) r+= *q++ * *pp++;
	  r*=s;
	  for(j=0,q=p1,pp=p1+k; j<nm ;++j) *pp++ -=r* *q++;
	 }
        for(j=1,q=p1+1; j<nm ;++j) *q++ *=t;
       }
      *p1=sv; e[i]= -h;
     }
    if(nm==1) e[i]= *p1;
   }
  ldvmat(a,v,n); atou1(a,m,n);
  qrbdu1(d,e,a,m,v,n);
  for(i=0; i<n ;++i){
    if(d[i]<0.){ d[i]= -d[i];
      for(j=0,p=v+i; j<n ;++j,p+=n) *p= - *p;
     }
   }
  free(w);
  return 0;
}


/*
Compute the singular values of a real m by n matrix A.

     int svdval(double *d,double *a,int m,int n)
       d = pointer to double array of dimension n
           (output = singular values of A)
       a = pointer to store of the m by n input matrix A
           (A is altered by the computation)
       m = number of rows in A
       n = number of columns in A (m>=n required)
      return value: status flag with:
               0 -> success
              -1 -> input error m < n
*/
int svdval(double *d,double *a,int m,int n)
{ double *p,*p1,*q,*w,*v;
  double s,h,u;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m,sizeof(double));
  for(i=0,mm=m,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(mm>1){
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; w[0]+=h;
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,u=0.; j<mm ;q+=n) u+=w[j++]* *q;
	  u*=s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=u*w[j++];
	 }
	*p= -h;
       }
     }
    p1=p+1;
    if(nm>1){
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
	s+= *p1*h; s=1./s; *p1+=h;
	for(k=n,ms=n*(m-i); k<ms ;k+=n){
	  for(j=0,q=p1,v=p1+k,u=0.; j<nm ;++j) u+= *q++ * *v++;
	  u*=s;
	  for(j=0,q=p1,v=p1+k; j<nm ;++j) *v++ -=u* *q++;
	 }
	*p1= -h;
       }
     }
   }

  for(j=0,p=a; j<n ;++j,p+=n+1){
    d[j]= *p; if(j!=n-1) w[j]= *(p+1); else w[j]=0.;
   }
  qrbdi(d,w,n);
  for(i=0; i<n ;++i) if(d[i]<0.) d[i]= -d[i];
  free(w);
  return 0;
}

/*
Transpose a complex square matrix in place A -> A~.

     void trncm(Cpx *a,int n)
       a = pointer to array of n by n complex matrix A
           The computation replaces A by its transpose
       n = dimension (dim(a)=n*n)

     */
void trncm(Cpx *a,int n)
{ Cpx s,*p,*q;
  int i,j,e;
  for(i=0,e=n-1; i<n-1 ;++i,--e,a+=n+1){
    for(j=0,p=a+1,q=a+n; j<e ;++j){
      s= *p; *p++ = *q; *q=s; q+=n;
     }
   }
}

/*
     Transpose a real square matrix in place A -> A~.

     void trnm(double *a,int n)
       a = pointer to array of n by n input matrix A
           This is overloaded by the transpose of A.
       n = dimension (dim(a)=n*n)

*/
void trnm(double *a,int n)
{ double s,*p,*q;
  int i,j,e;
  for(i=0,e=n-1; i<n-1 ;++i,--e,a+=n+1){
    for(p=a+1,q=a+n,j=0; j<e ;++j){
      s= *p; *p++ = *q; *q=s; q+=n;
     }
   }
}


/*
Generate a random unitary transformation U.

     void unitary(Cpx *u,int n)
       u = pointer to complex output array for U
       n = dimension (dim(u)=n*n)


     This function calls on the uniform random generator 'unfl' to
     produce random rotation angles. Therefore this random generator
     should be initialized by a call of 'setunfl' before calling
     'unitary' (see Chapter 7).
*/
void unitary(Cpx *u,int n)
{ int i,j,k,m; Cpx h,*v,*e,*p,*r;
  double *g,*q,a;
  m=n*n;
  g=(double *)calloc(n*n,sizeof(double));
  v=(Cpx *)calloc(m+n,sizeof(Cpx));
  e=v+m;
  h.re=1.; h.im=0.;
  for(i=0; i<n ;++i){
    a=tpi*unfl();
    e[i].re=cos(a); e[i].im=sin(a);
    a=h.re*e[i].re-h.im*e[i].im;
    h.im=h.im*e[i].re+h.re*e[i].im; h.re=a;
   }
  h.im= -h.im;
  for(i=0; i<n ;++i){
    a=e[i].re*h.re-e[i].im*h.im;
    e[i].im=e[i].re*h.im+e[i].im*h.re; e[i].re=a;
   }
  ortho(g,n);
  for(i=0,p=v,q=g; i<n ;++i){
    for(j=0; j<n ;++j) (p++)->re= *q++;
   }
  for(i=0,p=v; i<n ;++i){
    for(j=0,h=e[i]; j<n ;++j,++p){
      a=h.re*p->re-h.im*p->im;
      p->im=h.im*p->re+h.re*p->im; p->re=a;
     }
   }
  ortho(g,n);
  for(i=m=0,p=u; i<n ;++i,m+=n){
    for(j=0; j<n ;++j,++p){ 
      p->re=p->im=0.;
      for(k=0,q=g+m,r=v+j; k<n ;++k,r+=n){
	p->re+= *q*r->re; p->im+= *q++ *r->im;
       }
     }
   }
  free(g); free(v);
}


/*
Perform a unitary similarity transformation  C = T*B*T^.

     void utrncm(Cpx *cm,Cpx *a,Cpx *b,int n)
       a = pointer to the array of the transform matrix T
       b = pointer to the array of the input matrix B
       cm = pointer to output array of the transformed matrix C
       n = dimension (dim(cm)=dim(a)=dim(b)=n*n)
*/
void utrncm(Cpx *cm,Cpx *a,Cpx *b,int n)
{ Cpx z,*q0,*p,*s,*t;
  int i,j,k;
  q0=(Cpx *)calloc(n,sizeof(Cpx));
  for(i=0; i<n ;++i,++cm){
    for(j=0,t=b; j<n ;++j){
      z.re=z.im=0.;
      for(k=0,s=a+i*n; k<n ;++k,++s,++t){
        z.re+=t->re*s->re+t->im*s->im;
	z.im+=t->im*s->re-t->re*s->im;
       }
      q0[j]=z;
     }
    for(j=0,p=cm,t=a; j<n ;++j,p+=n){
      z.re=z.im=0.;
      for(k=0,s=q0; k<n ;++k,++t,++s){
	z.re+=t->re*s->re-t->im*s->im;
	z.im+=t->im*s->re+t->re*s->im;
       }
      *p=z;
     }
   }
  free(q0);
}


/*
Perform a unitary similarity transformation on a Hermitian
     matrix  H' = T*H*T^.

     void utrnhm(Cpx *hm,Cpx *a,Cpx *b,int n)
       a = pointer to the array of the transform matrix T
       b = pointer to the array of the Hermitian input matrix H
       hm = pointer to array containing Hermitian output matrix H'
       n = dimension (dim(cm)=dim(a)=dim(b)=n*n)
*/
void utrnhm(Cpx *hm,Cpx *a,Cpx *b,int n)
{ Cpx z,*q0,*p,*s,*t;
  int i,j,k;
  q0=(Cpx *)calloc(n,sizeof(Cpx));
  for(i=0; i<n ;++i){
    for(j=0,t=b; j<n ;++j){
      z.re=z.im=0.;
      for(k=0,s=a+i*n; k<n ;++k,++s,++t){
        z.re+=t->re*s->re+t->im*s->im;
	z.im+=t->im*s->re-t->re*s->im;
       }
      q0[j]=z;
     }
    for(j=0,p=hm+i,t=a; j<=i ;++j,p+=n){
      z.re=z.im=0.;
      for(k=0,s=q0; k<n ;++k,++t,++s){
	z.re+=t->re*s->re-t->im*s->im;
	z.im+=t->im*s->re+t->re*s->im;
       }
      *p=z; if(j<i){ z.im= -z.im; hm[i*n+j]=z;}
     }
   }
  free(q0);
}

/*
Multiply a vector by a matrix Vp = Mat*V.

     void vmul(double *vp,double *mat,double *v,int n)
       vp = pointer to array containing output vector
       mat = pointer to array containing input matrix in row order
       v = pointer to array containing input vector
       n = dimension of vectors (mat is n by n)
*/

void vmul(double *vp,double *mat,double *v,int n)
{ double s,*q; int k,i;
  for(k=0; k<n ;++k){
    for(i=0,q=v,s=0.; i<n ;++i) s+= *mat++ * *q++;
    *vp++ =s;
   }
}

/*
   Compute the inner product of two real vectors, p = u~*v.

     double vnrm(double *u,double *v,int n)
       u = pointer to array of input vector u
       v = pointer to array of input vector v
       n = dimension (dim(u)=dim(v)=n)
      return value: p = u~*v (dot product of u and v)
*/
double vnrm(double *u,double *v,int n)
{ double s; int i;
  for(i=0,s=0.; i<n ;++i) s+= *u++ * *v++;
  return s;
}



// FFT routines	
	
/*	    Compute the general radix FFT of a real input series.

     void fftgr(double *x,Cpx *ft,int n,int *kk,int inv)
        x = pointer to array of real input series (dimension = n)
        ft = pointer to complex structure array of Fourier transform
             output
        n = length of input and output series
        kk = pointer to array of factors of n (see pfac below)
        inv = control flag, with:
                inv='d' -> direct transform
                inv!='d' -> inverse transform
*/
void fftgr(double *x,struct complex *ft,int n,int *kk,int inv)
{ struct complex a,b,z,w,*d,*p,*f,*fb;
  double tpi=6.283185307179586,sc,q,*t;
  int *mm,*m,kp,i,j,k,jk,jl,ms,mp;
  mm=(int *)malloc((kk[0]+1)*sizeof(int));
  d=(struct complex *)malloc(kk[*kk]*sizeof(w));
  for(i=1,*mm=1,m=mm; i<=kk[0] ;++i,++m) *(m+1)= *m*kk[i];
  for(j=0,t=x; j<n ;++j){ jl=j; f=ft;
    for(i=1,m=mm; i<=kk[0] ;++i){
      k=n/ *++m; f+=(jl/k)* *(m-1); jl%=k;}
    f->re= *t++; f->im=0.;
   }
  if(inv=='d'){ for(i=0,sc=1./n,f=ft; i<n ;++i) (f++)->re*=sc;}
  for(i=1,m=mm; i<=kk[0] ;++i){
    ms= *m++; mp= *m; kp=kk[i]; q=tpi/mp; if(inv=='d') q= -q;
    a.re=cos(q); a.im=sin(q); b.re=cos(q*=ms); b.im=sin(q);
    for(j=0; j<n ;j+=mp){
      fb=ft+j; z.re=1.; z.im=0.;
      for(jk=0; jk<ms ;++jk,++fb){ p=d; w=z;
        for(k=0; k<kp ;++k,++p){ f=fb+mp-ms; *p= *f;
          while(f>fb){ f-=ms;
            sc=f->re+p->re*w.re-p->im*w.im;
            p->im=f->im+p->im*w.re+p->re*w.im; p->re=sc; }
          sc=w.re*b.re-w.im*b.im;
          w.im=w.im*b.re+w.re*b.im; w.re=sc;
         }
        for(k=0,f=fb,p=d; k<kp ;++k,f+=ms) *f= *p++;
        sc=z.re*a.re-z.im*a.im;
        z.im=z.im*a.re+z.re*a.im; z.re=sc;
       }
     }
   }
  free(d); free(mm);
}

static int kpf[26]={2,3,5,7,11,13,17,19,23,29,31,37,
  41,43,47,53,59,61,67,71,73,79,83,89,97,101};
int pfac(int n,int *kk,int fe)
{ int num,j,k,dc=1;
  if(fe=='e'){ n-=(n%2); dc=2;}
  for(;;n-=dc){ num=n; j=k=0;
    while(j<31){
      if(num%kpf[k]!=0){ if(k==25) break; ++k;}
      else{ kk[++j]=kpf[k]; num=num/kpf[k];
	if(num==1){ kk[0]=j; return n; } }
     }
   }
}
	

void pshuf(Cpx **pa,Cpx **pb,int *kk,int n)
{ int *mm,*m,i,j,k,jk; struct complex **p,**q;
  mm=(int *)malloc((kk[0]+1)*sizeof(int));
  for(i=1,*mm=1,m=mm; i<=kk[0] ;++i,++m) *(m+1)= *m*kk[i];
  for(j=0,p=pb; j<n ;++j){ jk=j; q=pa;
    for(i=1,m=mm; i<=kk[0] ;++i){
      k=n/ *++m; q+=(jk/k)* *(m-1); jk%=k; }
    *q= *p++;
   }
  free(mm);
}

void smoo(double *x,int n,int m)
{ double *p,*q,*pmax,*pmin,*pa,*pb,*ph;
  int ms; double s,t;
  ms=2*m+1; ph=x+n/2;
  p=pmin=(double *)calloc(ms,sizeof(*pmin));
  q=pmax=pmin+ms-1; s=t=0.;
  for(pa=x+m,pb=ph-m; pa>x ;){
    t+=(*p++ = *q-- = *pa--); s+= *pb++;}
  *ph=s/m; t=ms*(*pa++ = *q=t/m); s=1./ms;
  for(p=pmax,q=pmin,pb=pa+m; pa<ph;){
    t-= *q; if((++q)>pmax) q=pmin;
    if((++p)>pmax) p=pmin;
    t+=(*p= *pb++); *pa++ =s*t;
   }
  for(pa=ph-1,pb=ph+1; pa>x ;) *pb++ = *pa--;
  free(pmin);
}

int pwspec(double *x,int n,int m)
{ int j,kk[32]; double s;
  Cpx *p,*q;
  n=pfac(n,kk,'e');
  p=(struct complex *)malloc(n*sizeof(*p));
  fftgr(x,p,n,kk,'d');
  for(s=0.,j=0; j<n ;++j) s+=x[j]*x[j];
  for(s/=n,q=p,j=0; j<n ;++q)
    x[j++]=(q->re*q->re+q->im*q->im)/s;
  if(m) smoo(x,n,m);
  free(p); return n;
}

void ftuns(struct complex **pt,int n)
{ struct complex **p,**q; int j; double x,y,u,v,h=2.;
  p=pt+1; q=pt+n-1;
  if(n%2==0) n/=2; else n=n/2+1;
  for(j=1; j<n ;++j){
    x=((*p)->re+(*q)->re)/h; y=((*p)->im-(*q)->im)/h;
    u=((*p)->im+(*q)->im)/h; v=((*p)->re-(*q)->re)/h;
    (*p)->re=x; (*p++)->im=y;
    (*q)->re=u; (*q--)->im=v;
   }
}

void fftgc(Cpx **pc,struct complex *ft,int n,int *kk,int inv)
{ Cpx a,b,z,w,*d,*p,**f,**fb;
  double tpi=6.283185307179586,sc,q;
  int *mm,*m,kp,i,j,k,jk,jl,ms,mp;
  mm=(int *)malloc((kk[0]+1)*sizeof(int));
  d=(Cpx *)malloc(kk[*kk]*sizeof(*d));
  for(i=1,*mm=1,m=mm; i<=kk[0] ;++i,++m) *(m+1)= *m*kk[i];
  if(inv=='d'){
    for(j=0,p=ft; j<n ;++j){ jl=j; f=pc;
      for(i=1,m=mm; i<=kk[0] ;++i){
        k=n/ *++m; f+=(jl/k)* *(m-1); jl%=k;}
      *f=p++; } }
  if(inv<='e'){ for(i=0,sc=1./n,p=ft; i<n ;++i){
                  p->re*=sc; (p++)->im*=sc;} }
  else{ f=(Cpx **)malloc(n*sizeof(pc[0]));
        for(j=0; j<n ;++j) f[j]=pc[j];
        pshuf(pc,f,kk,n); free(f); }
  for(i=1,m=mm; i<=kk[0] ;++i){
    ms= *m++; mp= *m; kp=kk[i]; q=tpi/mp; if(inv<='e') q= -q;
    a.re=cos(q); a.im=sin(q); b.re=cos(q*=ms); b.im=sin(q);
    for(j=0; j<n ;j+=mp){
      fb=pc+j; z.re=1.; z.im=0.;
      for(jk=0; jk<ms ;++jk,++fb){ w=z;
        for(k=0,p=d; k<kp ;++k,++p){
          f=fb+mp-ms; *p= **f;
          while(f>fb){ f-=ms;
            sc=(*f)->re+p->re*w.re-p->im*w.im;
            p->im=(*f)->im+p->im*w.re+p->re*w.im; p->re=sc; }
          sc=w.re*b.re-w.im*b.im;
          w.im=w.im*b.re+w.re*b.im; w.re=sc;
         }
        for(k=0,f=fb,p=d; k<kp ;++k,++p,f+=ms) **f= *p;
        sc=z.re*a.re-z.im*a.im;
        z.im=z.im*a.re+z.re*a.im; z.re=sc;
       }
     }
   }
  free(d); free(mm);
}

void fft2(struct complex *ft,int m,int inv)
{ int n,i,j,k,mm,mp;
  double s,t,ang,tpi=6.283185307179586;
  struct complex u,w,*p,*q,*pf;
  n=1; n<<=m; pf=ft+n-1;
  for(j=0,p=ft; p<pf ;++p){ q=ft+j;
    if(p<q){ t=p->re; p->re=q->re; q->re=t;
             t=p->im; p->im=q->im; q->im=t; }
    for(mm=n/2; mm<=j ;mm/=2) j-=mm; j+=mm;
   }
  if(inv=='d') for(p=ft,s=1./n; p<=pf ;){
                  p->re*=s; (p++)->im*=s; }
  for(i=mp=1; i<=m ;++i){
    mm=mp; mp*=2; ang=tpi/mp; if(inv=='d') ang= -ang;
    w.re=cos(ang); w.im=sin(ang);
    for(j=0; j<n ;j+=mp){ p=ft+j;
      u.re=1.; u.im=0.;
      for(k=0; k<mm ;++k,++p){ q=p+mm;
        t=q->re*u.re-q->im*u.im;
        s=q->im*u.re+q->re*u.im;
        q->re=p->re-t; q->im=p->im-s;
        p->re+=t; p->im+=s;
        t=u.re*w.re-u.im*w.im;
        u.im=u.im*w.re+u.re*w.im; u.re=t;
       }
     }
   }
}

void fft2_d(struct complex *a,int m,int n,int f)
{ register int md,nd,i,j; struct complex *p,*q;
  register struct complex *r,*s;
  md=1<<m; nd=1<<n;
  for(i=0,p=a; i<md ;++i){
    fft2(p,n,f); p+=nd;
   }
  q=(struct complex *)calloc(sizeof(*q),md);
  for(i=0,p=a-nd; i<nd ;++i){
    for(r=q,s=p,j=0; j<md ;++j) *r++ = *(s+=nd);
    fft2(q,m,f);
    for(r=q,s=p++,j=0; j<md ;++j) *(s+=nd)= *r++;
   }
  free(q);
}


  // Fitting routines
  
  void chcof(double *c,int m,double (*func)(double))
{ double *p,*q,a,da,an,f; int j;
  ++m; q=c+m; a=1.570796326794897/m; da=a*2.;
  for(p=c; p<q ;) *p++ =0.;
  for(j=0; j<m ;++j,a+=da){
    *c+=(f=(*func)(cos(a)));
    for(p=c+1,an=0.; p<q ;) *p++ +=f*cos(an+=a);
   }
  for(p=c,an=2./m; p<q ;) *p++ *=an;
}

void chpade(double *c,double *a,int m,double *b,int n)
{ double *mat,*ps; int r,s,k;
  mat=(double *)calloc(n*n,sizeof(double));
  for(ps=mat,s=m+1,k=1; k<=n ;){
    for(r=1; r<=n ;++r) *ps++ =(c[abs(s-r)]+c[s+r])/2.;
    b[k++]= -c[s++];
   }
  solv(mat,b+1,n); b[0]=1.;
  for(s=0; s<=m ;++s){ a[s]=c[s];
    for(r=1; r<=n ;++r) a[s]+=(c[abs(s-r)]+c[s+r])*b[r]/2.;
   }
  a[0]/=2.; free(mat);
}


double csfit(double w,double *x,double *y,double *z,int m)
{ double s,t; int j,k;
  if(w<x[0] || w>x[m]) return 0.;
  for(j=1; w>x[j] ;++j); k=j-1;
  s=(t=w-x[k])*(x[j]-w); t/=(x[j]-x[k]);
  return (t*y[j]+(1.-t)*y[k]-s*(z[j]*(1.+t)+z[k]*(2.-t)));
}


void cspl(double *x,double *y,double *z,int m,double tn)
{ double h,s,t,*pa,*pb,*a,*b; int j;
  if(tn==0.) tn=2.;
  else{ h=sinh(tn); tn=(tn*cosh(tn)-h)/(h-tn);}
  pa=(double *)calloc(2*m,sizeof(double)); pb=pa+m;
  h=x[1]-x[0]; t=(y[1]-y[0])/h;
  for(j=1,a=pa,b=pb; j<m ;++j){
    *a++ =tn*((*b=x[j+1]-x[j])+h); h= *b++;
    z[j]=(s=(y[j+1]-y[j])/h)-t; t=s;
   }
  for(j=2,a=pa,b=pb; j<m ;++j){ h= *b/ *a;
    *++a-=h* *b++; z[j]-=h*z[j-1]; }
  z[0]=z[m]=0.;
  for(j=m-1; j>0 ;--j){ z[j]-= *b-- *z[j+1]; z[j]/= *a--;}
  free(pa);
}


void csplp(double *x,double *y,double *z,int m,double tn)
{ double h,s,t,u,*pa,*pb,*pc,*a,*b,*c;
  int j;
  if(tn==0.) tn=2.;
  else{ h=sinh(tn); tn=(tn*cosh(tn)-h)/(h-tn);}
  pa=(double *)calloc(3*m,sizeof(double)); pb=pa+m; pc=pb+m;
  *pc=h=x[1]-x[0]; t=u=(y[1]-y[0])/h;
  for(j=1,a=pa,b=pb; j<m ;++j){
    *a++ =tn*((*b=x[j+1]-x[j])+h); h= *b++;
    z[j]=(s=(y[j+1]-y[j])/h)-t; t=s;
   }
  z[m]=u-t; u= *pc; *a=(u+h)*tn;
  for(j=1,a=pa,b=pb,c=pc; j<m ;++j){ h= *b/ *a;
    *++a-=h* *b++; z[j+1]-=h*z[j]; s= *c++; *c= -s*h;
    }
  z[m-1]/= *--a; *--c+= *--b; *c/= *a--;
  for(j=m-2; j>0 ;--j){ h= *--b; s= *c--;
    z[j]-=h*z[j+1]; z[j]/= *a; *c-=h*s; *c/= *a--; }
  z[m]-=u*z[1]; s= *(pb-1)+ *(pc+m-1)-u* *pc;
  h=z[0]=(z[m]/=s);
  for(j=1,c=pc; j<m ;) z[j++]-= *c++ *h;
  free(pa);
}


double dcspl(double x,double *u,double *v,double *z,int m)
{ int i,k; double h,d;
  if(x>u[m] || x<u[0]) return 0.;
  for(i=1; x>u[i] ;++i);
  k=i-1; h=u[i]-u[k];
  x-=u[k]; x/=h;
  d=(v[i]-v[k])/h;
  return d-h*((z[i]-z[k])*(1.-3.*x*x)+z[k]*(3.-6.*x));
}

struct orpol {double cf,hs,df;};
typedef struct orpol Opol;


double evpsq(double x,Opol *c,int m)
{ int i; double f,s,t;
  f=s=c[m-1].cf; t=0.;
  for(i=m-2; i>=0 ;--i){
    f=c[i].cf+(x-c[i].df)*s-c[i+1].hs*t;
    t=s; s=f;
   }
  return f;
}
  
  
 double evpsqv(double x,Opol *c,int m,double *v,double sig)
{ int i; double f,h,s,t,r,z;
  f=s=c[m-1].cf; t=0.;
  for(i=m-2; i>=0 ;--i){
    f=c[i].cf+(x-c[i].df)*s-c[i+1].hs*t;
    t=s; s=f;
   }
  if(v!=0){
    r=s=1.; t=z=0.;
    for(i=0; i<m ;++i){
      r*=c[i].hs; z+=s*s/r;
      h=(x-c[i].df)*s-c[i].hs*t;
      t=s; s=h;
     }
    *v=sqrt(sig*z);
   }
  return f;
}


double fitval(double x,double *s,double *par,double (*fun)(double, double *),
	  double *v,int n)
{ 
  double *h,dl=1.e-8;

  double f,r,d; int i,j;
  f=(*fun)(x,par);
  for(i=0; i<n ;++i){
    par[i]+=dl; h[i]=((*fun)(x,par)-f)/dl;
    par[i]-=dl;
   }
  for(i=0,r=0.; i<n ;){
    for(j=0,d=0.; j<n ;) d+= *v++ *h[j++];
    r+=d*h[i++];
   }
  *s=sqrt(r); return f;
}

void setfval(int i,int n)
{ 
  double *h;
  if (i==0) 
    h = (double *)calloc(n,sizeof(*h));
  else free(h);
}


double ftch(double x,double *a,int m,double *b,int n)
{ double *p,y,t,tf,nu,de;
  y=2.*x; t=tf=0.;
  for(p=a+m; p>a ;){ nu= *p-- +y*t-tf; tf=t; t=nu;}
  nu= *p+x*t-tf; t=tf=0.;
  for(p=b+n; p>b ;){ de= *p-- +y*t-tf; tf=t; t=de;}
  de= *p+x*t-tf;
  return nu/de;
}

double gnlsq(double *x,double *y,int n,double *par,double *var,int m,
		double de,double (*func)( double, double *))
{ double *cp,*dp,*p,*q,*r,*s,*t;
  double err,f,z,ssq; int j,k,psinv(double *, int);
  cp=(double *)calloc(2*m,sizeof(double)); dp=cp+m;
  for(p=var,q=var+m*m; p<q ;) *p++ =0.;
  for(j=0,ssq=0.; j<n ;++j){ z=x[j];
    f=(*func)(z,par); err=y[j]-f; ssq+=err*err;
    for(k=0,p=par,r=cp; k<m ;++k){ *p+=de;
      *r++ =((*func)(z,par)-f)/de; *p++ -=de;
     }
    for(r=dp,s=cp,q=var; s<dp ;++s,q+=m+1){
      *r++ +=err* *s;
      for(t=s,p=q; t<dp ;) *p++ += *s* *t++;
     }
   }
  for(j=0,p=var; j<m ;++j,p+=m+1)
    for(k=j+1,q=p,r=p; k<m ;++k) *(q+=m)= *++r;
  if (psinv(var,m)==0){
    for(k=0,p=var,s=par; k<m ;++k,++s)
      for(j=0,t=dp; j<m ;++j) *s+= *p++ * *t++;
    free(cp); return ssq;
   }
  free(cp); return -1.;
}

double lsqsv(double *x,int *pr,double *var,double *d,double *b,double *v,
		int m,int n,double th)
{ double ssq,sig,*y,*p;
  int i,k;
  y=(double *)calloc(n,sizeof(double));
  for(i=n,ssq=0.,p=b+n; i<m ;++i,++p) ssq+= *p* *p;
  for(i=k=0; i<n ;++i){
    if(d[i]<th){ y[i]=0.; ssq+=b[i]*b[i];}
    else{ y[i]=b[i]/d[i]; ++k;}
   }
  *pr=k;
  vmul(x,v,y,n);
  if(var!=NULL && m>n){
    sig=ssq/(double)(m-n);
    for(i=0; i<n ;++i){
      if(d[i]<th) y[i]=0.; else y[i]=sig/(d[i]*d[i]);
     }
    smgen(var,y,v,n);
   }
  free(y);
  return ssq;
}

void psqcf(double *b,Opol *c,int m)
{ int i,j,k; double *sm,*s,u,v;
  if(m>1){
    sm=(double *)calloc(m*m,sizeof(double));
    sm[0]=sm[m+1]=1.; sm[1]= -c[0].df;
    for(i=2; i<m ;++i){ k=i-1;
      for(j=0,s=sm+i,v=0.; j<i ;++j,s+=m){
	*s=v-c[k].df* *(s-1)-c[k].hs* *(s-2);
	v= *(s-1);
       }
      *s=1.;
     }
    for(i=0; i<m ;++i){
      for(j=i,v=0.,s=sm+(m+1)*i; j<m ;++j) v+= *s++ *c[j].cf;
      b[i]=v;
     }
    free(sm);
   }
  else b[0]=c[0].cf;
}

void plsq(double *x,double *y,int n,Opol *cf,double *ssq,int m)
{ double *pm,*e,*p,*q;
  double f,s,t,u,w,tp; int i,j,k,l;
  pm=(double *)calloc(3*n,sizeof(double));
  for(i=0,w=u=0.,e=pm,p=e+n; i<n ;++i){
    w+=y[i]; u+=x[i]; *p++ =1.; *e++ =y[i];
   }
  cf[0].hs=tp=(double)n; cf[0].cf=w/tp; cf[0].df=u/tp; 
  for(k=1; k<m ;++k){ l=k-1;
    for(j=0,s=t=u=w=0.,e=pm,p=e+n,q=p+n; j<n ;++j){
      *e-= cf[l].cf* *p; s+= *e* *e;
      f=(x[j]-cf[l].df)* *p- cf[l].hs* *q;
      *q++ = *p; *p++ =f; w+=f* *e++;
      t+=(f*=f); if(k<m-1) u+=x[j]*f;
     }
    ssq[l]=s; cf[k].cf=w/t; if(k<m-1) cf[k].df=u/t;
    cf[k].hs=t/tp; tp=t;
   }
  l=m-1; t=cf[l].cf; cf[l].df=0.;
  for(j=0,e=pm,p=e+n,s=0.; j<n ;++j,++e){
    *e-=t* *p++; s+= *e* *e; 
   }
  ssq[l]=s;
  free(pm);
}


double pplsq(double *x,double *y,int n,double *bc,int m)
{ Opol *c; double *ss,sq;
  c=(Opol *)calloc(m,sizeof(Opol));
  ss=(double *)calloc(m,sizeof(double));
  plsq(x,y,n,c,ss,m);
  psqcf(bc,c,m);
  sq=ss[m-1];
  free(c); free(ss);
  return sq;
}


void psqvar(double *v,double sig,Opol *c,int m)
{ int i,j,k; double *sm,*s,*s1,*u,w,x;
  if(m>1){
    sm=(double *)calloc(m*m+m,sizeof(double));
    u=sm+m*m;
    sm[0]=sm[m+1]=1.; sm[1]= -c[0].df;
    for(i=2; i<m ;++i){ k=i-1;
      for(j=0,s=sm+i,w=0.; j<i ;++j,s+=m){
	*s=w-c[k].df* *(s-1)-c[k].hs* *(s-2);
	w= *(s-1);
       }
      *s=1.;
     }
    for(i=0,w=1.; i<m ;++i){
      w*=c[i].hs; u[i]=sig/w;
     }
    for(i=0,s=sm; i<m ;++i,s+=m){
      for(j=i,s1=sm+j*m; j<m ;++j,s1+=m){
	for(k=j,x=0.; k<m ;++k) x+=s[k]*s1[k]*u[k];
	v[i*m+j]=x; if(i!=j) v[j*m+i]=x;
       }
     }
    free(sm);
   }
  else v[0]=sig/c[0].hs;
}


int qrbdbv(double *d,double *e,double *b,double *v,int n)
{ int i,j,k,nn,jj,nm;
  double u,x,y,f,g,c,s,t,w,*p,*q;
  for(j=1,t=fabs(d[0]); j<n ;++j)
    if((s=fabs(d[j])+fabs(e[j-1]))>t) t=s;
  t*=1.e-15; nn=100*n; nm=n;
  for(j=0; n>1 && j<nn ;++j){
    for(k=n-1; k>0 ;--k){
      if(fabs(e[k-1])<t) break;
      if(fabs(d[k-1])<t){
        for(i=k,s=1.,c=0.; i<n ;++i){
          f=s*e[i-1]; g=d[i]; e[i-1]*=c;
          d[i]=u=sqrt(f*f+g*g); s= -f/u; c=g/u;
          p=b+k-1; q=b+i;
          w=c* *p+s* *q; *q=c* *q-s* *p; *p=w;
	 }
        break;
       }
     }
    y=d[k]; x=d[n-1]; u=e[n-2];
    f=(y+x)*(y-x)-u*u; s=y*e[k]; g=s+s;
    u=sqrt(f*f+g*g);
    c=sqrt((u+f)/(u+u)); s/=(c*u);
    for(i=k; i<n-1 ;++i){
      g=e[i];
      if(i>k){
	f=s*e[i]; g*=c;
	e[i-1]=u=sqrt(x*x+f*f);
	c=x/u; s=f/u;
       }
      f=c*y+s*g; g=c*g-s*y;
      for(jj=0,p=v+i; jj<nm ;++jj,p+=nm){
        q=p+1;
        w=c* *p+s* *q; *q=c* *q-s* *p; *p=w;
       }
      s*=d[i+1]; d[i]=u=sqrt(f*f+s*s);
      y=c*d[i+1]; c=f/u; s/=u;
      x=c*g+s*y; y=c*y-s*g;
      p=b+i; q=p+1;
      w=c* *p+s* *q; *q=c* *q-s* *p; *p=w;
     }
    e[n-2]=x; d[n-1]=y;
    if(fabs(x)<t) --n;
    if(n==k+1) --n; 
   }
  return j;
}


double qrlsq(double *a,double *b,int m,int n,int *f)
{ double *p,*q,*w,*v;
  double s,h,r;
  int i,j,k,mm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m,sizeof(double));
  for(i=0,mm=m,p=a; i<n ;++i,--mm,p+=n+1){
    if(mm>1){
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; w[0]+=h;
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r=r*s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
        *p= -h;
        for(j=0,q=b+i,r=0.; j<mm ;) r+=w[j++]* *q++;
        for(j=0,q=b+i,r*=s; j<mm ;) *q++ -=r*w[j++];
       }
     }
   }
  *f=solvru(a,b,n);
  for(j=n,q=b+j,s=0.; j<m ;++j,++q) s+= *q* *q;
  free(w);
  return s;
} 



double qrvar(double *v,int m,int n,double ssq)
{ int j,k;
  double z,*p,*q,*r,*s,*t;
  if(m>n) ssq/=(double)(m-n);
  ruinv(v,n);
  for(j=0,p=v; j<n ;++j,p+=n+1){
    for(q=v+j,t=p-j; q<=p ;q+=n){
      for(k=j,r=p,s=q,z=0.; k<n ;++k) z += *r++ * *s++;
      *t++ =(*q=z);
     }
   }
  for(j=0,k=n*n,p=v; j<k ;++j) *p++ *=ssq;
  return ssq;
}



double seqlsq(double *x,double *y,int n,double *par,double *var,int m,
		double de,double (*func)(double, double *),int kf)
{ double *pd,*pc,*pmax,*p,*q,*r,*s,*t;
  double err,ssq,f,z; int j;
  pd=(double *)calloc(2*m,sizeof(double)); pc=pd+m;
  if(kf==0){ for(p=var,pmax=p+m*m; p<pmax ;) *p++ =0.;
    for(p=var; p<pmax ;p+=m+1) *p=1.; }
  for(j=0,ssq=0.,pmax=pc+m; j<n ;++j){ z=x[j];
    f=(*func)(z,par); err=y[j]-f; ssq+=err*err;
    for(p=pd,q=par; p<pc ;){ *q+=de;
      *p++ =((*func)(z,par)-f)/de; *q++ -=de;
     }
    for(p=pc,q=pd,r=var,z=1.; p<pmax ;){ *p=0.;
      for(s=pd; s<pc ;) *p+= *r++ * *s++;
      z+= *p++ * *q++;
     }
    z=sqrt(z); err/=z;
    for(p=pc,q=par; p<pmax ;) *q++ += err*(*p++ /=z);
    for(p=pc,t=var; p<pmax ;++p,t+=m+1)
      for(q=p,r=s=t; q<pmax ;r+=m) *r=(*s++ -= *p* *q++);
   }
  free(pd); return ssq;
}


int sv2lsq(double *d,double *a,double *b,int m,double *v,int n)
{ double *p,*p1,*q,*pp,*w,*e;
  double s,t,h,r,sv;
  int i,j,k,mm,nm,ms;
  if(m<n) return -1;
  w=(double *)calloc(m+n,sizeof(double)); e=w+m;
  for(i=0,mm=m,p=a; i<n ;++i,--mm,p+=n+1){
    if(mm>1){ h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; w[0]+=h;
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  r=r*s;
	  for(j=0,q=p+k; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(j=0,q=b+i,r=0.; j<mm ;) r+=w[j++]* *q++;
        for(j=0,q=b+i,r*=s; j<mm ;) *q++ -=w[j++]*r;
       }
      d[i]= -h;
     }
    if(mm==1) d[i]= *p;
   }
  for(i=0,p=a; i<n ;++i){
    for(j=0; j<n ;++j,++p){
      if(j<i) *p=0.;
      else if(j==i) *p=d[i];
     }
   }
  for(i=0,mm=n,nm=n-1,p=a; i<n ;++i,--mm,--nm,p+=n+1){
    if(i && mm>1){ sv=h=0.;
      for(j=0,q=p,s=0.; j<mm ;++j,q+=n){
	w[j]= *q; s+= *q* *q;
       }
      if(s>0.){
	h=sqrt(s); if(*p<0.) h= -h;
	s+= *p*h; s=1./s; t=1./(w[0]+=h);
        sv=1.+fabs(*p/h);
	for(k=1,ms=n-i; k<ms ;++k){
	  for(j=0,q=p+k,r=0.; j<mm ;q+=n) r+=w[j++]* *q;
	  for(j=0,q=p+k,r*=s; j<mm ;q+=n) *q-=r*w[j++];
	 }
        for(j=0,q=b+i,r=0.; j<mm ;) r+=w[j++]* *q++;
        for(j=0,q=b+i,r*=s; j<mm ;) *q++ -=r*w[j++];
       }
      *p=sv; d[i]= -h;
     }
    if(mm==1) d[i]= *p;
    p1=p+1;
    if(nm>1){ sv=h=0.;
      for(j=0,q=p1,s=0.; j<nm ;++j,++q) s+= *q* *q;
      if(s>0.){
	h=sqrt(s); if(*p1<0.) h= -h;
        sv=1.+fabs(*p1/h);
	s+= *p1*h; s=1./s; t=1./(*p1+=h);
	for(k=n,ms=n*(n-i); k<ms ;k+=n){
	  for(j=0,q=p1,pp=p1+k,r=0.; j<nm ;++j) r+= *q++ * *pp++;
	  for(j=0,q=p1,pp=p1+k,r*=s; j<nm ;++j) *pp++ -=r* *q++;
	 }
	for(j=1,q=p1+1; j<nm ;++j) *q++ *=t;
       }
      *p1=sv; e[i]= -h;
     }
    if(nm==1) e[i]= *p1;
   }
  ldvmat(a,v,n);
  qrbdbv(d,e,b,v,n);
  for(i=0; i<n ;++i){
    if(d[i]<0.){ d[i]= -d[i];
      for(j=0,p=v+i; j<n ;++j,p+=n) *p= - *p;
     }
   }
  free(w);
  return 0;
} 



// Complex


struct complex cmul(Cpx s,Cpx t)
{ double u;
  u=s.re*t.re-s.im*t.im;
  s.im=s.im*t.re+s.re*t.im; s.re=u;
  return s;
}
struct complex cdiv(Cpx s,Cpx t)
{ double u,r;
  r=t.re*t.re+t.im*t.im;
  u=(s.re*t.re+s.im*t.im)/r;
  s.im=(s.im*t.re-s.re*t.im)/r; s.re=u;
  return s;
}
struct complex cadd(Cpx s,Cpx t)
{ s.re+=t.re; s.im+=t.im;
  return s;
}
struct complex csub(Cpx s,Cpx t)
{ s.re-=t.re; s.im-=t.im;
  return s;
}
struct complex crmu(double a,Cpx z)
{ z.re*=a; z.im*=a; return z;
}
struct complex cimu(double b,Cpx z)
{ double u;
  u=z.re*b; z.re= -z.im*b; z.im=u;
  return z;
}
struct complex ccng(Cpx z)
{ z.im= -z.im; return z;
}
struct complex cdef(double r,double i)
{ struct complex s;
  s.re=r; s.im=i; return s;
}
double cabs(Cpx c)
{ return sqrt(c.re*c.re+c.im*c.im);
}
double cnrm(Cpx z)
{ return z.re*z.re+z.im*z.im;
}


struct complex cexp(Cpx z)
{ double s,c,u;
/* s=sincos(z.im,&c); */ 
  s=sin(z.im); c=cos(z.im);
  u=exp(z.re);
  z.re=u*c; z.im=u*s;
  return z;
}
struct complex clog(Cpx z)
{ double r;
  r=z.re*z.re+z.im*z.im;
  z.im=atan2(z.im,z.re);
  z.re=ldexp(log(r),-1);
  return z;
}


struct complex csinh(Cpx z)
{ double c,s,u,v;
/*  s=sincos(z.im,&c); */
  s=sin(z.im); c=cos(z.im);
  u=exp(z.re); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  z.re=v*c; z.im=u*s;
  return z;
}
struct complex ccosh(Cpx z)
{ double c,s,u,v;
/*  s=sincos(z.im,&c); */
  s=sin(z.im); c=cos(z.im);
  u=exp(z.re); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  z.re=c*u; z.im=v*s;
  return z;
}
struct complex ctanh(Cpx z)
{ double c,s,u,v,d;
/*  s=sincos(z.im,&c); */
  s=sin(z.im); c=cos(z.im);
  u=exp(z.re); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  d=c*c+v*v; z.re=u*v/d; z.im=s*c/d;
  return z;
}


struct complex csqrt(Cpx z)
{ double r;
  r=sqrt(z.re*z.re+z.im*z.im);
  r=sqrt(ldexp(r+fabs(z.re),-1));
  if(r==0.) z.re=z.im=0.;
  else{
    if(z.re>=0.){ z.re=r; z.im=ldexp(z.im/r,-1);}
    else{ z.re=ldexp(fabs(z.im)/r,-1);
      if(z.im>=0.) z.im=r; else z.im= -r;
     }
   }
  return z;
}


struct complex casinh(Cpx z)
{ struct complex u;
  u.re=1.; u.im=0.;
  u=csqrt(cadd(u,cmul(z,z)));
  u.re+=z.re; u.im+=z.im;
  return clog(u);
}
struct complex cacosh(Cpx z)
{ struct complex u; int kf;
  if(z.im==0. && z.re< -1.) kf=1; else kf=0;
  u.re=1.; u.im=0.;
  u=csqrt(csub(cmul(z,z),u));
  z.re+=u.re; z.im+=u.im; z=clog(z);
  if(z.re<0.){ z.re= -z.re; z.im= -z.im;}
  if(kf) z.im= -z.im;
  return z;
}
struct complex catanh(Cpx z)
{ struct complex u;
/*
  int kf;
  if(z.im==0. && z.re>1.) kf=1; else kf=0;
*/
  u.re=1.; u.im=0.;
  u=cdiv(cadd(u,z),csub(u,z));
  u=crmu(.5,clog(u));
/*
  if(kf) u.im= -u.im;
*/
  return u;
}


struct complex casin(Cpx z)
{ struct complex u;
  u.re=1.; u.im=0.;
  u=csqrt(csub(u,cmul(z,z)));
  u.re-=z.im; u.im+=z.re; z=clog(u);
  u.re=z.im; u.im= -z.re;
  return u;
}
struct complex cacos(Cpx z)
{ struct complex u;
  u.re=1.; u.im=0.;
  u=csqrt(csub(u,cmul(z,z)));
  z.re-=u.im; z.im+=u.re; z=clog(z);
  u.re=z.im; u.im= -z.re;
  return u;
}
struct complex catan(Cpx z)
{ struct complex u;
/*
  int kf;
  if(z.re==0. && z.im<0.) kf=1; else kf=0;
*/
  u.re= -z.im; u.im=z.re; z.re=1.; z.im=0.;
  u=cdiv(cadd(z,u),csub(z,u));
  u=cimu(-.5,clog(u));
/*
  if(kf) u.re= -u.re;
*/
  return u;
}


struct complex csin(Cpx z)
{ double c,s,u,v;
/* s=sincos(z.re,&c); */
  s=sin(z.re); c=cos(z.re);
  u=exp(z.im); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  z.re=u*s; z.im=c*v;
  return z;
}
struct complex ccos(Cpx z)
{ double c,s,u,v;
/* s=sincos(z.re,&c); */
  s=sin(z.re); c=cos(z.re);
  u=exp(z.im); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  z.re=c*u; z.im= -s*v;
  return z;
}
struct complex ctan(Cpx z)
{ double c,s,u,v,d;
/* s=sincos(z.re,&c); */
  s=sin(z.re); c=cos(z.re);
  u=exp(z.im); v=1./u;
  u=ldexp(u+v,-1); v=u-v;
  d=c*c+v*v; z.re=s*c/d; z.im=u*v/d;
  return z;
}


 
 // Geometry routines
void crossp(double *h,double *u,double *v)
{ h[0]=u[1]*v[2]-u[2]*v[1];
  h[1]=u[2]*v[0]-u[0]*v[2];
  h[2]=u[0]*v[1]-u[1]*v[0];
}

double dotp(double *u,double *v,int m)
{ double s,*umx;
  for(umx=u+m,s=0.; u<umx ;) s+= *u++ * *v++;
  return s;
} 


void euler(double *pv,int m,double a,double b,double c)
{ double t,cn,sn,*p,*q; int j,k;
  for(k=0; k<3 ;++k){
    switch(k){
       case 0: cn=cos(c); sn=sin(c); p=pv; q=p+1; break;
       case 1: cn=cos(b); sn= -sin(b); p=pv; q=p+2; break;
       case 2: cn=cos(a); sn=sin(a); p=pv; q=p+1;
     }
    for(j=0; j<m ;++j,p+=3,q+=3){
      t=cn* *p-sn* *q; *q=cn* *q+sn* *p; *p=t; }
   }
}


int htgaaa(double a,double b,double c,double *as)
{ double y,sa,sb,sc;
  double pi=3.141592653589793;
  if(a+b+c>=pi) return -1;
  sa=sin(a); sb=sin(b); sc=sin(c);
  a=cos(a); b=cos(b); c=cos(c);
  y=a*b*c; y+=y;
  y=sqrt(y+a*a+b*b+c*c-1.);
  a=y/(sb*sc); as[0]=log(a+sqrt(a*a+1.));
  b=y/(sa*sc); as[1]=log(b+sqrt(b*b+1.));
  c=y/(sa*sb); as[2]=log(c+sqrt(c*c+1.));
  return 0;
}


double htgarea(double a,double b,double c)
{ double pi=3.141592653589793;
  return pi-a-b-c;
}


int htgasa(double a,double cc,double b,double *ans)
{ double sa,sb,x;
  double pi=3.141592653589793;
  if(a<0. || b<0.) return -1;
  sa=sin(a); a=cos(a); sb=sin(b); b=cos(b);
  x=sa*sb*cosh(cc)-a*b;
  ans[1]=atan2(sqrt(1.-x*x),x);
  cc=sinh(cc)*sa*sb/sin(ans[1]);
  x=cc/sb; ans[0]=log(x+sqrt(x*x+1.));
  x=cc/sa; ans[2]=log(x+sqrt(x*x+1.));
  return 0;
}


void htgsas(double a,double g,double b,double *an)
{ double sa,sb,sg;
  double pi=3.141592653589793;
  g=.5*(pi-g); sg=sin(g); g=cos(g);
  b=.5*(a-b); a-=b;
  sa=sinh(a); a=cosh(a); sb=sinh(b); b=cosh(b);
  an[0]=atan2(sg*b,g*a);
  an[2]=atan2(sg*sb,g*sa);
  g=sg*b/sin(an[0]);
  an[1]=log(g+sqrt(g*g-1));
  a=an[0]-an[2]; an[0]+=an[2]; an[2]=a;
  an[1]+=an[1];
}


int htgsss(double a,double b,double c,double *ang)
{ double s;
  s=.5*(a+b+c);
  if(s-a<0. || s-b<0. || s-c<0.) return -1;
  a=cosh(a); b=cosh(b); c=cosh(c);
  s=a*b*c; s+=s;
  s=sqrt(1.-a*a-b*b-c*c+s);
  ang[0]=atan2(s,b*c-a);
  ang[1]=atan2(s,c*a-b);
  ang[2]=atan2(s,a*b-c);
  return 0;
}

double leng(double *a,double *b,int n)
{ double s,t; int j;
  if(b!=NULL){
    for(s=0.,j=0; j<n ;++j){ t= *a++ - *b++; s+=t*t;}
   }
  else{ for(s=0.,j=0; j<n ;++j){ t= *a++; s+=t*t;} }
  return sqrt(s);
}


double metpr(double *u,double *a,double *v,int n)
{ double r,s; int i,j;
  for(i=0,s=0.; i<n ;){
    for(j=0,r=0.; j<n ;) r+= *a++ *v[j++];
    s+=r*u[i++];
   }
  return s;
}

static void strig(double *p,double a,double b,double c);
void rotax(double *v,double az,double pa,double ang,int k)
{ static double ca,sa,cb,sb,cn,sn;
  double a[3],t,pi=3.14159265358979; int fg;
  if(k==0){ if(pa==0.){ ca=cb=0.; cn=ang;}
    else if(pa==pi){ ca=cb=0.; cn= -ang;}
    else{ if(ang<0.){ fg=1; ang= -ang;} else fg=0;
      strig(a,pa,ang,pa);
      if(fg==0){ ca=az-a[0]; cn=pi-az-a[0];}
      else{ a[1]= -a[1]; cn=a[0]-az; ca=az+a[0]-pi;}
     }
    sa=sin(ca); ca=cos(ca); sb=sin(a[1]); cb=cos(a[1]);
    sn=sin(cn); cn=cos(cn);
   }
  t=cn*v[0]-sn*v[1]; v[1]=cn*v[1]+sn*v[0]; v[0]=t;
  t=cb*v[0]+sb*v[2]; v[2]=cb*v[2]-sb*v[0]; v[0]=t;
  t=ca*v[0]-sa*v[1]; v[1]=ca*v[1]+sa*v[0]; v[0]=t;
}
static void strig(double *p,double a,double b,double c)
{ double as,cs,bb;
  as=sin(a); a=cos(a); cs=sin(c); c=cos(c);
  bb=c*a+cs*as*cos(b);
  if(bb>1.) bb=1.; else if(bb< -1.) bb= -1.;
  b=cs*as*sin(b);
  p[0]=atan2(b,c-a*bb);
  p[1]=acos(bb);
  p[2]=atan2(b,a-c*bb);
}

void scalv(double *r,double s,int n)
{ double *mx;
  for(mx=r+n; r<mx ;) *r++ *=s;
}


int stgaaa(double a,double b,double c,double *ang)
{ double s;
  double pi=3.141592653589793;
  if(a+b+c<=pi) return -1;
  a=cos(a); b=cos(b); c=cos(c);
  s=a*b*c; s+=s;
  s=sqrt(1.-a*a-b*b-c*c-s);
  ang[0]=atan2(s,b*c+a);
  ang[1]=atan2(s,a*c+b);
  ang[2]=atan2(s,a*b+c);
  return 0;
}


double stgarea(double a,double b,double c)
{ double pi=3.141592653589793;
  return (a+b+c-pi);
}

int stgasa(double a,double c,double b,double *ang)
{ double sa,sb,sc; int f;
  double pi=3.141592653589793;
  if(a>=0. && b>=0.) f=0;
  else if(a<0. && b<0.){ a= -a; b= -b; f=1;}
  else return -1;
  c*=.5; sc=sin(c); c=cos(c);
  b=.5*(a-b); a-=b;
  sa=sin(a); a=cos(a); sb=sin(b); b=cos(b);
  ang[0]=atan2(sc*b,c*a);
  ang[2]=atan2(sc*sb,c*sa);
  ang[1]=atan2(sa/cos(ang[2]),a/cos(ang[0]));
  a=ang[0]-ang[2]; ang[0]+=ang[2]; ang[2]=a;
  ang[1]+=ang[1];
  if(ang[1]>0.) ang[1]=pi-ang[1];
  else ang[1]= -ang[1]-pi;
  if(f) ang[1]= -ang[1];
  return 0;
}

void stgsas(double a,double g,double b,double *ang)
{ double sa,sb,sg;
  double pi=3.141592653589793;
  if(g>0.) g=.5*(pi-g); else g= -.5*(pi+g);
  sg=sin(g); g=cos(g);
  b=.5*(a-b); a-=b;
  sa=sin(a); a=cos(a); sb=sin(b); b=cos(b);
  ang[0]=atan2(sg*b,g*a);
  ang[2]=atan2(sg*sb,g*sa);
  if((sg=g*sa/cos(ang[2]))<0.707) ang[1]=asin(sg);
  else ang[1]=acos(g*a/cos(ang[0]));
  a=ang[0]-ang[2]; ang[0]+=ang[2]; ang[2]=a;
  ang[1]+=ang[1];
}

int stgsss(double a,double b,double c,double *ang)
{ double s;
  s=.5*(a+b+c);
  if(s-a<0. || s-b<0. || s-c<0.) return -1;
  a=cos(a); b=cos(b); c=cos(c);
  s=a*b*c; s+=s;
  s=sqrt(1.-a*a-b*b-c*c+s);
  ang[0]=atan2(s,a-b*c);
  ang[1]=atan2(s,b-a*c);
  ang[2]=atan2(s,c-a*b);
  return 0;
}

double trgarea(double a,double b,double c)
{ double s;
  s=a+b+c;
  s=sqrt(s*(s-a-a)*(s-b-b)*(s-c-c));
  return .25*s;
}

int trgasa(double a,double ss,double b,double *asn)
{ double h;
  double pi=3.141592653589793;
  if(a<0. || b<0.) return -1;
  asn[1]=h=pi-a-b;
  h=sin(h);
  asn[0]=ss*sin(a)/h;
  asn[2]=ss*sin(b)/h;
  return 0;
}

void trgsas(double a,double g,double b,double *ans)
{ double u,s;
  u=a*b; s=u*sin(g);
  u*=cos(g); u+=u; s+=s;
  a*=a; b*=b;
  u=a+b-u; ans[1]=sqrt(u);
  ans[0]=atan2(s,b+u-a);
  ans[2]=atan2(s,a+u-b);
}

int trgssa(double a,double b,double ba,double *an)
{ double x,y,z,v;
  x=a*cos(ba); ba=a*sin(ba);
  a*=a; b*=b;
  if((y=b-ba*ba)<0.) return -1;
  else y=sqrt(y);
  an[0]=x+y; v=an[0]*an[0];
  z=an[0]*ba; z+=z;
  an[1]=atan2(z,a+b-v);
  an[2]=atan2(z,b+v-a);
  if(x>y){
    an[3]=x-y; v=an[3]*an[3];
    z=an[3]*ba; z+=z;
    an[4]=atan2(z,a+b-v);
    an[5]=atan2(z,b+v-a);
   }
  else an[3]=an[4]=an[5]=0.;
  return 0;
}

int trgsss(double a,double b,double c,double *ang)
{ double s;
  s=.5*(a+b+c);
  if(s-a<0. || s-b<0. || s-c<0.) return -1;
  s+=s;
  s=sqrt(s*(s-a-a)*(s-b-b)*(s-c-c));
  a*=a; b*=b; c*=c;
  ang[0]=atan2(s,b+c-a);
  ang[1]=atan2(s,a+c-b);
  ang[2]=atan2(s,a+b-c);
  return 0;
}


void trvec(double *c,double *a,double *b,int n)
{ double *mx;
  for(mx=c+n; c<mx ;) *c++ = *a++ + *b++;
}


 // Integration routines
 double chintg(double *a,int m,double (*func)(double))
{ double f,t,tf,aj,adel,*pf,*pe,*p,*ps;
  int j;
  pf=(double *)calloc(2*m,sizeof(double)); pe=pf+m;
  ++m; adel=3.1415926535897932/m;
  for(p=pf,ps=pe,j=1,aj=adel; p<pe ;aj+=adel){
    t=cos(aj); a[j++]=f=sin(aj);
    *ps++ =2.*t; *p++ =(*func)(t)*f;
   }
  for(j=m-1,ps=pe+m-2; j>0 ;--ps,--j){
    for(p=pe-1,t=tf=0.; p>=pf ;){ f= *ps*t-tf+ *p--; tf=t; t=f;}
    a[j]*=t*2./(j*m);
   }
  for(j=1,a[0]=0.; j<m ;++j){
    if(j%2==0) a[0]-=a[j]; else a[0]+=a[j]; }
  for(j=m-1,f=0.; j>m-4 ;--j) if((t=fabs(a[j]))>f) f=t;
  free(pf); return f;
}

int deqsy(double *y,int n,double a,double b,int nd,double te,
	  int (*fsys)( double, double *, double *))
{ 
  double h,x,ht,st,*dp,*fp,*fq,*ap,*p,*q,*pt;
  int m,j,k;
  fp=(double *)calloc(13*n,sizeof(double));
  fq=fp+n; dp=fq+n; ap=dp+n; h=(b-a)/nd;
  for(m=0; m>=0 ;){ ++m; (*fsys)(x=a,y,dp);
    for(j=0,p=fp,q=fq,pt=dp; p<fq ;)
      *p++ =(*q++ =y[j++])+h* *pt++;
    for(j=1,st=2.*h; j<nd ;++j){ (*fsys)(x+=h,fp,dp);
      for(p=fp,q=fq,pt=dp; p<fq ;){
        ht= *q+st* *pt++; *q++ = *p; *p++ =ht; }
     }
    (*fsys)(x+=h,fp,dp);
    for(p=fp,q=fq,pt=dp; p<fq ;){
      *p+= *q++ +h* *pt++; *p++ /=2.; }
    for(j=1,k=1,pt=ap; j<m ;++j){ k*=4;
      for(p=dp,q=fp; q<fq ;){
        *p=(*q- *pt)/(k-1); *pt++ = *q; *q++ += *p++; }
     }
    for(q=fp; q<fq ;) *pt++ = *q++;
    h/=2.; nd*=2;
    if(m>1){
       for(k=0,p=fp,q=dp; p<fq ;){
         if(fabs(*q++)>te*fabs(*p++)){ k=1; break;}
        }
       if(k==0) break;
       if(m==10) m= -m;
      }
   }
  for(p=y,q=fp; q<fq ;) *p++ = *q++;
  free(fp); return m;
}

double fchb(double x,double *a,int m)
{ double *p,y,t,tf,de;
  y=2.*x; t=tf=0.;
  for(p=a+m; p>a ;){ de= *p-- +y*t-tf; tf=t; t=de;}
  return (*p+x*t-tf);
}


double fintg(double a,double b,int n,double te,double (*func)(double))
{ int j,k,m; double s,t,x,h,ap[10],*p;
  s=((*func)(b)+(*func)(a))/2.;
  h=(b-a)/n; x=a;
  for(j=1; j<n ;++j) s+=(*func)(x+=h);
  ap[0]=s*h;
  for(m=1;;){ ++m; x=a-h/2.;
    for(j=0; j<n ;++j) s+=(*func)(x+=h);
    t=s*h/2.;
    for(j=k=1,p=ap; j<m ;++j){
      k*=4; x=(t- *p)/(k-1); *p++ =t; t+=x; }
    *p=t;
    if(fabs(x)<te*fabs(t)) return t;
    if(m==10) return pow(2.,126.);
    h/=2.; n*=2;
   }
}


// Roots

#define Abs(x) ( ((x)<0.)?-(x):(x) )
static double fev(double *x,double *py,double *ps,
	double c,double (*func)(double *));
int optmiz(double *x,int n,double (*func)(double *),double de,double test,int max)
{ double fs,fp,fa,fb,fc,s,sa,sb,sc; int k,m;
  double *pd,*ps,*py,*pg,*ph,*p,*q,*r;
  pd=(double *)calloc(n*(n+4),sizeof(double));
  ps=pd+n; py=ps+n; pg=py+n; ph=pg+n;
  for(p=ph,q=ph+n*n; p<q ;p+=n+1) *p=1.;
  for(p=x,q=pg,fb=(*func)(x); q<ph ;){
    *p+=de; *q++ =((*func)(x)-fb)/de; *p++ -=de;}
  for(m=0; m<max ;++m){
    for(p=ps,r=ph; p<py ;++p){ *p=0.;
      for(q=pg; q<ph ;) *p-= *r++ * *q++; }
    fp=fa=fb; sa=sb=0.; sc=1.;
    for(;;){ if((fc=fev(x,py,ps,sc,func))>fb) break;
      fa=fb; sa=sb; fb=fc; sb=sc; sc*=2.; }
    if(sc==1.){ sb=.5;
      for(;;){ if((fb=fev(x,py,ps,sb,func))<fa||sb<1.e-3) break;
         fc=fb; sc=sb; sb/=2.;} }
    for(k=0; k<3 ;++k){ s=(fc-fa)/(sc-sa);
      if((fs=(s-(fb-fa)/(sb-sa))/(sc-sb))<0.) break;
      if((s=(sa+sc-s/fs)/2.)==sb) s-=(sb-sa)/5.;
      fs=fev(x,py,ps,s,func);
      if(fs<fb){ if(s<sb){ sc=sb; fc=fb;} else{ sa=sb; fa=fb;}
         sb=s; fb=fs; }
      else{ if(s<sb){ sa=s; fa=fs;} else{ sc=s; fc=fs;} }
     }
    for(p=x,r=ps; r<py ;){ *r*=sb; *p++ += *r++;}
    if(Abs(fp-fb)<test){ free(pd); return (m+1);}
    for(p=x,q=pg,r=pd; q<ph ;){
      *p+=de; fa=((*func)(x)-fb)/de; *p++ -=de;
      *r++ =fa- *q; *q++ =fa; }
    for(p=py,r=ph; p<pg ;++p){ *p=0.;
      for(q=pd; q<ps ;) *p+= *r++ * *q++; }
    for(p=py,q=ps,r=pd,sa=sb=0.; p<pg ;){
      sa+= *r* *p++; sb+= *q++ * *r++; }
    sa=1.+sa/sb;
    for(p=ps,q=py,r=ph; p<py ;++p,++q){
      for(k=0; k<n ;++k)
        *r++ +=(*(ps+k)* *p*sa- *(py+k)* *p- *(ps+k)* *q)/sb;
       }
   }
  free(pd); return 0;
}
static double fev(double *x,double *py,double *ps,double c,double (*func)(double *))
{ double *p,*q,*r;
  for(p=x,q=py,r=ps; r<py ;) *q++ = *p++ +c* *r++;
  return (*func)(py);
}

double optsch(double (*func)( double ),double a,double b,double test)
{ double x,y,f1,f2,r=.61803399,s;
  s=b-a; x=a+r*s; y=b-r*s;
  f1=(*func)(x); f2=(*func)(y);
  while(1){ s*=r;
    if(f2>f1){ if(s<test) return x;
        a=y; y=x; x=a+r*s; f2=f1; f1=(*func)(x);}
    else{ if(s<test) return y;
        b=x; x=y; y=b-r*s; f1=f2; f2=(*func)(y);}
   }
}

 
  int plrt(double *cof,int n,Cpx *root,double ra,double rb)
{ double a,b,s,t,w; int itr,pat; struct complex *pr;
  double *cs,*cf,*hf,*p,*q,*ul,test=1.e-28;
  cs=cf=(double *)calloc(2*n,sizeof(double)); hf=cf+n;
  pr=root+n-1; ul=hf+n-1;
  if(rb<0.) rb=ra*ra-rb*rb; else rb=ra*ra+rb*rb; ra*= -2.;
  q=cof+n; s= *q--;
  for(p=cf; p<hf ;) *p++ = *q-- /s;
  for(itr=pat=0;;){
    if(itr==0){
      if(n>2){ a=ra; b=rb;}
      else if(n==2){ a= *cf++; b= *cf;}
      else if(n==1){ pr->re= -(*cf); pr->im=0.; free(cs); return 0;}
     }
    s= -a/2.; t=s*s-b;
    if(t>=0.){ t=sqrt(t); pr->re=s+t; (pr--)->im=0.;
               pr->re=s-t; (pr++)->im=0.; }
    else{ t=sqrt(-t); pr->re=s; (pr--)->im=t;
          pr->re=s; (pr++)->im= -t; }
    if(n==2){ free(cs); return 0;}
    for(p=hf,q=cf; q<hf ;) *p++ = *q++;
    for(p=hf,w=1.; p<ul ;){
      *p++ -=a*w; *p-=b*w; w=*(p-1); }
    t= -(*p--); t+=pr->re* *p; s=pr->im* *p;
    if(t*t+s*s<test){ pr-=2; ul-=2; n-=2; itr=pat=0;
      for(p=cf,q=hf; p<ul ;) *p++ = *q++;
     }
    else if(++itr<30){
           for(p=hf,w=1.; p<ul-2 ;){
             *p++ -=w*a; *p-=w*b; w= *(p-1); }
      t= *p++; q=p+1; s=t*t+w*(b*w-a*t);
      b+=(w*(*p*b- *q*a)+ *q*t)/s; a+=(*p*t- *q*w)/s;
     }
    else{ if(pat==3){ free(cs); return n;}
          itr=0; if(pat++ %2) ra= -ra; else rb= -rb;}
   }
}



Cpx polyc(Cpx z,double *cof,int n)
{ int i; Cpx py; double s;
  for(i=n-1,py.re=cof[n],py.im=0.; i>=0 ;--i){
    s=py.re*z.re-py.im*z.im;
    py.im=py.im*z.re+py.re*z.im; py.re=s+cof[i];
   }
  return py;
}


double secrt(double (*func)(double),double x,double dx,double test)
{ double f,fp,y; int k;
  y=x-dx; fp=(*func)(y);
  for(k=0;;++k){ f=(*func)(x);
    dx=f*(x-y)/(f-fp); fp=f; y=x; x-=dx;
    if(((dx<0.)?-dx:dx)<test || k==50)
      return x;
   }
}

int solnl(double *x,double *f,double (*fvec[])( double *),
		int n,double test)
{ double sc,del,delp; int i,j,k;
  double *p,*q,*r,*s,*pth,*py,*ps,*pb;
  pth=(double *)calloc(n*n+3*n,sizeof(*pth));
  py=pth+n*n; ps=py+n; pb=ps+n;
  for(i=0; i<n*n ;i+=n+1) *(pth+i)=1.;
  for(i=0,p=f,s=ps,del=0.; s<pb ;){
    *p=(*fvec[i++])(x); del+= *p* *p; *s++ = -(*p++); }
  for(k=0; k<20*n ;++k){
    for(i=0,sc=1.; i<5 ;++i,sc/=2.){
      for(p=pb,s=ps,q=x; s<pb ;){ *s*=sc; *p++ = *q++ + *s++;}
      for(j=0,p=py,delp=0.; j<n ;++p){
        *p=(*fvec[j++])(pb); delp+= *p* *p;}
      if(delp<del) break;
     }
    del=delp;
    for(p=x,q=pb,r=py,s=f; r<ps ;){ *p++ = *q++;
      sc= *r- *s; *s++ = *r; *r++ =sc; }
    if(del<test){ free(pth); return 1;}
    for(j=0,p=pb,r=py,sc=0.; r<ps ;){ *p=0.; s=pth+(j++);
      for(q=ps; q<pb ;s+=n) *p+= *s* *q++;
      sc+= *r++ * *p++;
     }
    for(s=ps,r=pth; s<pb ;++s)
      for(q=py; q<ps ;) *s-= *r++ * *q++;
    for(s=ps,r=pth; s<pb ;++s)
      for(q=pb,j=0; j<n ;++j) *r++ += *s* *q++/sc;
    for(s=ps,r=pth; s<pb ;++s){ *s=0.;
      for(p=f,j=0; j<n ;++j) *s-= *r++ * *p++;
     }
   }
  free(pth); return 0;
}

int solnx(double *x,double *f,double (*fvec[])(double *),double *jm,
       int n,double test)
{ double sc,del,delp; int i,j,k;
  double *p,*q,*r,*s,*pth,*py,*ps,*pb;
  pth=(double *)calloc(n*n+3*n,sizeof(*pth));
  py=pth+n*n; ps=py+n; pb=ps+n;
  for(i=0; i<n*n ;++i) *(pth+i)=jm[i]; minv(pth,n);
  for(i=0,p=f,s=ps,del=0.; s<pb ;){
    *p=(*fvec[i++])(x); del+= *p* *p; *s++ = -(*p++); }
  for(k=0; k<20*n ;++k){
    for(i=0,sc=1.; i<5 ;++i,sc/=2.){
      for(p=pb,s=ps,q=x; s<pb ;){ *s*=sc; *p++ = *q++ + *s++;}
      for(j=0,p=py,delp=0.; j<n ;++p){
        *p=(*fvec[j++])(pb); delp+= *p* *p;}
      if(delp<del) break;
     }
    del=delp;
    for(p=x,q=pb,r=py,s=f; r<ps ;){ *p++ = *q++;
      sc= *r- *s; *s++ = *r; *r++ =sc; }
    if(del<test){ free(pth); return 1;}
    for(j=0,p=pb,r=py,sc=0.; r<ps ;){ *p=0.; s=pth+(j++);
      for(q=ps; q<pb ;s+=n) *p+= *s* *q++;
      sc+= *r++ * *p++;
     }
    for(s=ps,r=pth; s<pb ;++s)
      for(q=py; q<ps ;) *s-= *r++ * *q++;
    for(s=ps,r=pth; s<pb ;++s)
      for(q=pb,j=0; j<n ;++j) *r++ += *s* *q++/sc;
    for(s=ps,r=pth; s<pb ;++s){ *s=0.;
      for(p=f,j=0; j<n ;++j) *s-= *r++ * *p++;
     }
   }
  free(pth); return 0;
}


// Special functions

double airy(double x,int df)
{ double f,y,a,b,s; int p;
  double u=.258819403792807,v=.355028053887817;
  if(x<=1.7 && x>= -6.9){ y=x*x*x/9.;
    if(df){ b= -(a=2./3.); v*=x*x/2.; u= -u;}
    else{ a= -(b=1./3.); u*= -x;}
    for(p=1,f=u+v;;++p){
      v*=y/(p*(a+=1.)); u*=y/(p*(b+=1.));
      f+=(s=u+v); if(fabs(s)<1.e-14) break; }
   }
  else{ s=1./sqrt(v=3.14159265358979); y=fabs(x);
    if(df) s*=pow(y,.25); else s/=pow(y,.25);
    y*=2.*sqrt(y)/3.;
    if(x>0.){ a=12./pow(y,.333); p=a*a;
      if(df) a= -7./36.; else a=5./36.;
      b=2.*(p+y); f=1.; u=x=0.; s*=exp(-y)/2.;
      for(; p>0 ;--p,b-=2.){
        y=(b*f-(p+1)*x)/(p-1+a/p); x=f; u+=(f=y); }    
      if(df) f*= -s/u; else f*=s/u;
     }
    else{ x=y-v/4.; y*=2.; b=.5; f=s; v=0.;
      if(df) a=2./3.; else a=1./3.;
      for(p=1; (u=fabs(s))>1.e-14 ;++p,b+=1.){
        s*=(a+b)*(a-b)/(p*y); if(fabs(s)>=u) break;
        if(!(p&1)){ s= -s; f+=s;} else v+=s; }
      if(df) f=f*sin(x)+v*cos(x); else f=f*cos(x)-v*sin(x);
     }
   }
  return f;
}

double amelp(double u,double k)
{ double a,b,cs[10]; int m,n=1;
  a=1.; b=sqrt(1.-k*k);
  for(m=0; (k=a-b)>4.e-15 ;++m){
    cs[m]=k/2.; k=a+b; b=sqrt(a*b);
    a=k/2.; cs[m]/=a; n*=2;
   }
  for(u*=n*a,--m; m>=0 ;--m)
    u=(u+asin(cs[m]*sin(u)))/2.;
  return u;
}

double biry(double x,int df)
{ double f,y,a,b,s; int p;
  double u=.258819403792807,v=.355028053887817;
  if(x<=7.6 && x>= -6.9){ y=x*x*x/9.;
    if(df){ b= -(a=2./3.); u*=(f=sqrt(3.)); v*=f*x*x/2.;}
    else{ a= -(b=1./3.); v*=(f=sqrt(3.)); u*=f*x;}
    for(p=1,f=u+v;;++p){
      v*=y/(p*(a+=1.)); u*=y/(p*(b+=1.)); f+=(s=u+v);
      if(fabs(s)<1.e-14*(1.+fabs(f))) break; }
   }
  else{ s=1./sqrt(v=3.14159265358979); y=fabs(x);
    if(df) s*=pow(y,.25); else s/=pow(y,.25);
    y*=2.*sqrt(y)/3.; b=.5;
    if(df) a=2./3.; else a=1./3.;
    if(x>0.){ s*=exp(y); f=s; y*= -2.;
      for(p=1; (u=fabs(s))>1.e-14 ;++p,b+=1.){
        s*=(a+b)*(a-b)/(p*y); if(fabs(s)>=u) break; f+=s; }
     }
    else{ x=y-v/4.; y*=2.; f=s; v=0.;
      for(p=1; (u=fabs(s))>1.e-14 ;++p,b+=1.){
        s*=(a+b)*(a-b)/(p*y); if(fabs(s)>=u) break;
        if(!(p&1)){ s= -s; f+=s;} else v+=s; }
      if(df) f=f*cos(x)-v*sin(x); else f= -(f*sin(x)+v*cos(x));
     }
   }
  return f;
}


double drbes(double x,double v,int f,double *p)
{ double y,jbes(double u,double s),nbes(double u,double s);
  double ibes(double u,double s),kbes(double u,double s);
  if(x==0.){
    switch(f){
      case 'j':
      case 'i': if(v==1.) return .5;
                if(v==0. || v>1.) return 0.;
      default : break;  }
    return HUGE_VAL;
   }
  if(p!=0L) y= *p*v/x; else y=0.;
  switch(f){
    case 'j': if(p==0L && v>0.) y=jbes(v,x)*v/x;
              return y-jbes(v+1.,x);
    case 'y': if(p==0L && v>0.) y=nbes(v,x)*v/x;
              return y-nbes(v+1.,x);
    case 'i': if(p==0L && v>0.) y=ibes(v,x)*v/x;
              return y+ibes(v+1.,x);
    case 'k': if(p==0L && v>0.) y=kbes(v,x)*v/x;
              return y-kbes(v+1.,x);
   } return 0.;
}


double drspbes(double x,int n,int f,double *p)
{ double y;
  double jspbes(int m,double a),yspbes(int m,double a);
  double kspbes(int m,double a);
  if(x==0.){
    if(f=='j'){ if(n==1) return 1./3.; else return 0.;}
    return HUGE_VAL;
   }
  if(p!=NULL) y= *p*n/x; else y=0.;
  switch(f){
    case 'j': if(p==NULL && n) y=jspbes(n,x)*n/x;
              return y-jspbes(++n,x);
    case 'y': if(p==NULL && n) y=yspbes(n,x)*n/x;
              return y-yspbes(++n,x);
    case 'k': if(p==NULL && n) y=kspbes(n,x)*n/x;
              y-=kspbes(++n,x);
              if(x>0.) return y; else return -y;
   }
  return 0.;
}

double felp(double an,double k,double *pk,double *pz,double *ph)
{ double a,b,c,s,h; int m=1;
  double pi=3.14159265358979;
  a=1.; b=sqrt(1.-k*k); s=h=0.;
  while((c=(a-b)/2.)>.5e-15){ m*=2;
    if((k=atan(b*tan(an)/a))<0.) k+=pi;
    if((k-=fmod(an,pi))>2.) k-=pi;
    an+=an+k; k=a+b; b=sqrt(a*b); a=k/2.;
    h+=c*a*m; s+=c*sin(an);
   }
  *pk=pi/(2.*a); an/=m*a;
  if(pz!=NULL){
    *pz=s+(h=1.-h)*an; *ph=h* *pk;}
  return an;
}

double g2elp(double an,double bn,double k,double as,double bs,double ds)
{ double a,b,d,s,r,f,h; int m=1,q=0;
  double pi=3.14159265358979;
  double gsng2(double *pa,double *pb,double *pc,double b,double an,
		  double bn);
  a=1.; b=sqrt(1.-k*k); r=s=0.;
  if(ds<0.)
    if((r=gsng2(&as,&bs,&ds,b,an,bn))==HUGE_VAL) return r;
  if(an<0.){ an= -an; q=1;}
  while(a-b>1.e-15){ m*=2;
    if((k=atan(b*tan(an)/a))<0.) k+=pi;
    if((k-=fmod(an,pi))>2.) k-=pi; an+=an+k;
    if((k=atan(b*tan(bn)/a))<0.) k+=pi;
    if((k-=fmod(bn,pi))>2.) k-=pi; bn+=bn+k;
    k=a+b; b=sqrt(a*b); a=k/2.;
    d=(as-bs)/(2.*a*m);
    k=as+bs; bs=as+ds*bs; as=k/2.;
    bs/=(k=1.+ds); ds=b*k*k/(4.*a*ds);
    if((f=1.-b*ds/a)>1.e-9){
      d/=2.*(f=sqrt(f)); h=f*sin(bn); f*=sin(an);
      s+=d*log((1.+f)/(1.-f)); r+=d*log((1.+h)/(1.-h)); }
    else if(f< -1.e-9){
      d/=(f=sqrt(-f)); h=f*sin(bn); f*=sin(an);
      s+=d*atan(f); r+=d*atan(h); }
    else{ s+=d*sin(an); r+=d*sin(bn);}
   }
  if(q) return as*(bn+an)/(m*a)+r+s;
  return as*(bn-an)/(m*a)+r-s;
}


double gaml(double x)
{ double g,h;
  for(g=1.; x<30. ;g*=x,x+=1.); h=x*x;
  g=(x-.5)*log(x)-x+.918938533204672-log(g);
  g+=(1.-(1./6.-(1./3.-1./(4.*h))/(7.*h))/(5.*h))/(12.*x);
  return g;
}


double gelp(double an,double k,double as,double bs,double ds,
		 double *pg,double *pf,double *pk)
{ double a,b,d,s,f; int m=1;
  double pi=3.14159265358979;
  double gsng(double *pa,double *pb,double *pc,double b,double an);
  a=1.; b=sqrt(1.-k*k); s=0.;
  if(ds<0.)
    if((s=gsng(&as,&bs,&ds,b,an))==HUGE_VAL) return s;
  while(a-b>1.e-15){ m*=2;
    if((k=atan(b*tan(an)/a))<0.) k+=pi;
    if((k-=fmod(an,pi))>2.) k-=pi;
    an+=an+k; k=a+b; b=sqrt(a*b);
    a=k/2.; d=(as-bs)/(2.*a*m);
    k=as+bs; bs=as+ds*bs; as=k/2.;
    bs/=(k=1.+ds); ds=b*k*k/(4.*a*ds);
    if((f=1.-b*ds/a)>1.e-9){
      d/=2.*(f=sqrt(f)); f*=sin(an);
      s+=d*log((1.+f)/(1.-f));  }
    else if(f< -1.e-9){
      d/=(f=sqrt(-f)); f*=sin(an); s+=d*atan(f); }
    else s+=d*sin(an);
   }
  f=an/(m*a);
  if(pg!=NULL){ k=pi/(2.*a); *pg=as*k;
    if(pf!=NULL){ *pf=f; *pk=k;}
   }
  return as*f+s;
}

double gsng(double *pa,double *pb,double *pc,double b,double an)
{ double r,s,t,u;
  r= *pa- *pb; u=b* *pc; s=1.-u; t=b*b-u;
  *pc= *pa; *pa= *pb+r/s; *pb= *pc+r*u/t;
  *pc=(t/=s)/b; t=sqrt(-u*t); r*=(-u/(2.*s*t));
  s=sin(an); u=(1.-b*b)*s*s; t*=tan(an)/sqrt(1.-u);
  if(fabs(1.-t)<1.e-15) return HUGE_VAL;
  return r*log(fabs((1.+t)/(1.-t)));
}

static double ze=1.e-15;
double gsng2(double *pa,double *pb,double *pc,double b,
		double an,double bn)
{ double r,s,t,ta,tb,u;
  r= *pa- *pb; u=b* *pc; s=1.-u; t=b*b-u;
  *pc= *pa; *pa= *pb+r/s; *pb= *pc+r*u/t;
  *pc=(t/=s)/b; t=sqrt(-u*t); r*=(-u/(2.*s*t));
  u=1.-b*b;
  s=sin(bn); tb=t*tan(bn)/sqrt(1.-u*s*s);
  s=sin(an); ta=t*tan(an)/sqrt(1.-u*s*s);
  if(fabs(1.-ta)<ze || fabs(1.-tb)<ze) return HUGE_VAL;
  return r*log(fabs((1.+tb)*(1.-ta)/((1.-tb)*(1.+ta))));
}

double ibes(double v,double x)
{ double y,s,t,tp,gaml(double w); int p,m;
  y=x-9.; if(y>0.) y*=y; tp=v*v*.2+25.;
  if(y<tp){ x/=2.; m=x;
    if(x>0.) s=t=exp(v*log(x)-gaml(v+1.));
    else{ if(v>0.) return 0.; else if(v==0.) return 1.;}
    for(p=1,x*=x;;++p){ t*=x/(p*(v+=1.)); s+=t;
      if(p>m && t<1.e-13*s) break;
     }
   }
  else{ double u,a0=1.57079632679490;
    s=t=1./sqrt(x*a0); x*=2.; u=0.;
    for(p=1,y=.5; (tp=fabs(t))>1.e-14 ;++p,y+=1.){
      t*=(v+y)*(v-y)/(p*x); if(y>v && fabs(t)>=tp) break;
      if(!(p&1)) s+=t; else u-=t;
     }
    x/=2.; s=cosh(x)*s+sinh(x)*u;
   }
  return s;
}

double jbes(double v,double x)
{ double y,s,t,tp,gaml(double z); int p,m;
  y=x-8.5; if(y>0.) y*=y; tp=v*v/4.+13.69;
  if(y<tp){ x/=2.; m=x;
    if(x>0.) s=t=exp(v*log(x)-gaml(v+1.));
    else{ if(v>0.) return 0.; else if(v==0.) return 1.;}
    for(p=1,x*= -x;;++p){ t*=x/(p*(v+=1.)); s+=t;
      if(p>m && fabs(t)<1.e-13) break;
     }
   }
  else{ double u,a0=1.57079632679490;
    s=t=1./sqrt(x*a0); x*=2.; u=0.;
    for(p=1,y=.5; (tp=fabs(t))>1.e-14 ;++p,y+=1.){
      t*=(v+y)*(v-y)/(p*x); if(y>v && fabs(t)>=tp) break;
      if(!(p&1)){ t= -t; s+=t;} else u-=t;
     }
    y=x/2.-(v+.5)*a0; s=cos(y)*s+sin(y)*u;
   }
  return s;
}

double jspbes(int n,double x)
{ double y,s,t,v,u,a0=1.57079632679490;
  double gaml(double a); int p,m;
  if(x==0.){ if(n==0) return 1.; else return 0.;}
  v=n+.5; y=1.+.68*n;
  if(x<y){ x/=2.; m=x;
    s=t=exp(n*log(x)-gaml(v+1.))*sqrt(a0/2.);
    for(p=1,x*=x;;++p){
      t*= -x/(p*(v+=1.)); s+=t;
      if(p>m && fabs(t)<1.e-13*fabs(s)) break; }
   }
  else{ s=t=1./x; x*=2.; u=0.;
    for(p=1,y=.5; y<v ;++p,y+=1.){
      t*=(v+y)*(v-y)/(p*x);
      if(!(p&1)){ t= -t; s+=t;} else u-=t; }
    y=x/2.-(v+.5)*a0; s=cos(y)*s+sin(y)*u;
   }
  return s;
}

double nome(double k,double *pk,double *pkp)
{ double a,b,s,r,pi2=1.57079632679490;
  a=s=1.; b=sqrt(1.-k*k);
  while(a-b>4.e-15 || s-k>4.e-15){
    r=a+b; b=sqrt(a*b); a=r/2.;
    r=s+k; k=sqrt(s*k); s=r/2.;
   }
  *pk=pi2/a; a*=(*pkp=pi2/s);
  return exp(-2.*a);
}



double rcbes(void)
{ 
 double f,h,v,x;
 int d,ty;

double t;
  if(d=='u'){
    switch(ty){
      case 'j':
      case 'y': t=f*v/x-h; break;
      case 'i': t=h-f*v/x; break;
      case 'k': t=h+f*v/x; break; }
    h=f; f=t; v+=1.;
   }
  else{
    switch(ty){
      case 'j':
      case 'y': t=h*v/x-f; break;
      case 'i': t=f+h*v/x; break;
      case 'k': t=f-h*v/x; break; }
    f=h; h=t; v-=1.;
   }
  return t;
}
void setrcb(double u,double y,int fl,int dr,double *pf,double *ph)
{ 
 double f,h,v,x;
 int d,ty;

  double jbes(double u,double x),ibes(double u,double x);
  double nbes(double u,double x),kbes(double u,double x);
  if(dr=='d') u-=1.;
  switch(fl){
    case 'j': h=jbes(u,y); f=jbes(u+1.,y); break;
    case 'y': h=nbes(u,y); f=nbes(u+1.,y); break;
    case 'i': h=ibes(u,y); f=ibes(u+1.,y); break;
    case 'k': h=kbes(u,y); f=kbes(u+1.,y); break;
   }
  x=y/2.; ty=fl; d=dr;
  if(dr=='u'){ v=u+1.; *pf=h; *ph=f;}
  else{ v=u; *pf=f; *ph=h;}
}


double rcspbs(void)
{ double t;
 double f,h,v,x;
 int d,ty;
  if(d=='u'){
    switch(ty){
      case 'j':
      case 'y': t=f*v/x-h; break;
      case 'k': t=h+f*v/x; break;
     }
    h=f; f=t; v+=1.;
   }
  else{
    switch(ty){
      case 'j':
      case 'y': t=h*v/x-f; break;
      case 'k': t=f-h*v/x; break;
     }
    f=h; h=t; v-=1.;
   }
  return t;
}


void setrcsb(int n,double y,int fl,int dr,double *pf,double *ph)
{
 double f,h,v,x;
 int d,ty;

 double jspbes(int i,double a),yspbes(int i,double a);
  double kspbes(int i,double a);
  if(dr=='d') --n;
  switch(fl){
    case 'j': h=jspbes(n,y); f=jspbes(n+1,y); break;
    case 'y': h=yspbes(n,y); f=yspbes(n+1,y); break;
    case 'k': h=kspbes(n,y); f=kspbes(n+1,y); break;
   }
  x=y/2.; ty=fl; d=dr;
  if(dr=='u'){ v=n+1.5; *pf=h; *ph=f;}
  else{ v=n+.5; *pf=f; *ph=h;}
}
 
 
static double q,qq,kf;
double theta(double u,int n)
{ double c,s,c0,s0,f,r,z;
  u*=kf; c0=cos(2.*u); s0=sin(2.*u);
  switch(n){
    case 0:
    case 3: f=1.; r=2.*q; z=q;
            c=c0; s=s0; break;
    case 1:
    case 2: f=0.; r=2.*pow(q,.25); z=1.;
            c=cos(u); s=sin(u);
   }
  if(n==0){ r= -r; z= -z;} if(n==1) z= -z;
  while(fabs(r)>1.e-16){
    if(n==1) f+=r*s; else f+=r*c;
    u=c*c0-s*s0; s=s*c0+c*s0; c=u;
    z*=qq; r*=z;
   }
  return f;
}
void stheta(double k)
{ double nome(double k,double *pk,double *pkp);
  double pi2=1.57079632679490;
  q=nome(k,&kf,&qq); qq=q*q; kf=pi2/kf;
}


double yspbes(int n,double x)
{ double v,y,s,t,u,a0=1.57079632679490; int p;
  v=n+.5; if(x==0.) return HUGE_VAL;
  s=t=1./x; x*=2.; u=0.;
  for(p=1,y=.5; y<v ;++p,y+=1.){
    t*=(v+y)*(v-y)/(p*x);
    if(!(p&1)){ t= -t; s+=t;} else u+=t;
   }
  y=x/2.-(v+.5)*a0; s=sin(y)*s+cos(y)*u;
  return s;
}


// Time Series
struct mcof {double cf; int lag;};
struct fmod {int fac; double val;};

struct mcof *par,*pma,*pfc;

int nar,nma,nfc,np,ndif;
static int kst,kd,max,mxm,*kz; static double *pm,*pz;
double drfmod(struct fmod y,double *dr)
{ struct mcof *p,*q; double yp,*pf,*pd,*pl,sa; int j;
  yp=sa=(pfc+y.fac)->cf;
  for(j=0,pf=dr; j<nfc ;j++,pf++){
     if(j==y.fac) *pf=1.; else *pf=0.;
     for(p=par,q=p+nar; p<q ;p++)
        if(*(kz+(kst+p->lag)%max)==j) *pf -= p->cf;
    }
  if(ndif){ pd=pz+2*max+np*mxm; pl=dr+nfc;
     for(pf=dr; pf<pl ;pf++)
        for(j=0; j<ndif ;j++){
           sa= *pf- *pd; *pd++ = *pf; *pf=sa;}
     for(j=0; j<ndif ;j++){
        sa=yp- *pd; *pd++ =yp; yp=sa;}
    }
  for(p=par,q=p+nar; p<q ;p++){
     *pf= *(pz+(kst+p->lag)%max);
     yp += p->cf * *pf++;}
  for(p=pma,q=p+nma; p<q ;p++){
     *pf= *(pm+(kst+p->lag)%max);
     yp += p->cf * *pf++;}
  if(nma){ pl=dr+np;
      for(pf=dr,pd=pz+2*max; pf<pl ;pd++,pf++)
         for(p=pma,q=p+nma; p<q ;p++)
             *pf += p->cf * *(pd+((kd+p->lag)%mxm)*np);
      kd=(kd+mxm-1)%mxm; pd=pz+2*max+np*kd;
      for(pf=dr; pf<pl;) *pd++ = *pf++;
     }
  kst=(kst+max-1)%max; *(pm+kst)=(yp-=y.val);
  *(pz+kst)=y.val-sa; *(kz+kst)=y.fac;
  return -yp;
}
void setdrf(int k)
{ if(k){ max=mxm=kst=kd=0; np=nfc+nma+nar;
      if(nar) max=(par+nar-1)->lag+1;
      if(nma && (mxm=(pma+nma-1)->lag+1)>max) max=mxm;
      pz=(double *)calloc(2*max+np*mxm+ndif*(nfc+1),sizeof(double));
      kz=(int *)calloc(max,sizeof(int));
      pm=pz+max; }
  else{ free(pz); free(kz);}
}


double drmod(double y,double *dr)
{ struct mcof *p,*q; double yp,*pf,*pd,*pl;
  yp=0.; pl=dr+np;
  for(pf=dr,p=par,q=p+nar; p<q ;++p){
    *pf= *(pz+(kst+p->lag)%max);
    yp+= p->cf * *pf++;
   }
  for(p=pma,q=p+nma; p<q ;++p){
    *pf= *(pm+(kst+p->lag)%max);
    yp+= p->cf * *pf++;
   }
  if(nma){
    for(pf=dr,pd=pz+2*max; pf<pl ;++pd,++pf)
      for(p=pma,q=p+nma; p<q ;++p)
        *pf+= p->cf * *(pd+((kd+p->lag)%mxm)*np);
    kd=(kd+mxm-1)%mxm; pd=pz+2*max+np*kd;
    for(pf=dr; pf<pl ;) *pd++ = *pf++;
   }
  kst=(kst+max-1)%max; *(pm+kst)=(yp-=y);
  *(pz+kst)=y; return -yp;
}
void setdr(int k)
{ if(k){ max=mxm=kst=kd=0; np=nma+nar;
    if(nar) max=(par+nar-1)->lag+1;
    if(nma && (mxm=(pma+nma-1)->lag+1)>max) max=mxm;
    pz=(double *)calloc(2*max+np*mxm,sizeof(double));
    pm=pz+max;
   }
  else free(pz);
}

double evfmod(struct fmod y)
{ struct mcof *p,*q; double yp,sa,*pd; int j;
  yp=sa=(pfc+y.fac)->cf;
  if(ndif){ pd=pz+2*max;
     for(j=0; j<ndif ;j++){
        sa=yp- *pd; *pd++ =yp; yp=sa;}
    }
  for(p=par,q=p+nar; p<q ;p++)
     yp+= *(pz+(kst+p->lag)%max) * p->cf;
  for(p=pma,q=p+nma; p<q ;p++)
     yp+= *(pm+(kst+p->lag)%max) * p->cf;
  kst=(kst+max-1)%max; *(pm+kst)=(yp-=y.val);
  *(pz+kst)=y.val-sa; *(kz+kst)=y.fac;
  return -yp;
}
void setevf(int k)
{ int mxm;
  if(k){ max=mxm=kst=0; np=nfc+nma+nar;
      if(nar) max=(par+nar-1)->lag+1;
      if(nma && (mxm=(pma+nma-1)->lag+1)>max) max=mxm;
      pz=(double *)calloc(2*max+ndif,sizeof(double));
      kz=(int *)calloc(max,sizeof(int));
      pm=pz+max; }
  else{ free(pz); free(kz);}
}

double evmod(double y)
{ struct mcof *p,*q; double yp;
  for(yp=0.,p=par,q=p+nar; p<q ;++p)
    yp+= *(pz+(kst+p->lag)%max) * p->cf;
  for(p=pma,q=p+nma; p<q ;++p)
    yp+= *(pm+(kst+p->lag)%max) * p->cf;
  kst=(kst+max-1)%max; *(pm+kst)=(yp-=y);
  *(pz+kst)=y; return -yp;
}
void setev(int k)
{ int mxm;
  if(k){ max=mxm=kst=0; np=nma+nar;
    if(nar) max=(par+nar-1)->lag+1;
    if(nma && (mxm=(pma+nma-1)->lag+1)>max) max=mxm;
    pz=(double *)calloc(2*max,sizeof(double));
    pm=pz+max;
   }
  else free(pz);
}

static void oprj(double *var,int n,int m)
{ double s,*pd,*p; int i,j;
  pd=(double *)calloc(n,sizeof(double));
  for(i=0,p=pd,s=0.; i<n ;++i){ *p=0.;
     for(j=0; j<m ;++j) *p+=var[i+n*j];
     if(i<m) s+= *p++; else ++p; }
  for(i=0,p=var; i<n ;++i)
     for(j=0; j<n ;++j) *p++ -= *(pd+i)* *(pd+j)/s;
  free(pd);
}

double xmean(double *x,int n)
{ double xm; int j;
  for(j=0,xm=0.; j<n ;) xm+=x[j++];
  xm/=n;
  for(j=0; j<n ;) x[j++]-=xm;
  return xm;
}


#define MXD 6
static double f[MXD];
double sdiff(double y,int nd,int k)
{ double s;
  if(k==0) for(k=0; k<nd ;) f[k++]=0.;
  for(k=0; k<nd ;){
    s=y-f[k]; f[k++]=y; y=s; }
  return s;
}
double fixts(double *x,int n,double *var,double *cr)
{ double *cp,*p,*q,*r,*s,*pmax;
  struct mcof *pp; double e,ssq,drmod(double,double *);
  int j,k,psinv(double *,int);
  cp=(double *)calloc(np,sizeof(double));
  for(p=var,pmax=p+np*np; p<pmax ;) *p++ =0.;
  setdr(1); pmax=cr+np;
  for(j=0,ssq=0.; j<n ;){
    e=drmod(x[j++],cr); ssq+=e*e;
    for(k=0,r=cp,s=cr,q=var; s<pmax ;++s,q+= ++k){
      *r++ +=e* *s;
      for(p=s; p<pmax ;) *q++ += *s * *p++;
     }
   }
  for(k=1,p=var,r=p+np*np; k<np ;p+= ++k)
    for(q=p+np; q<r ;q+=np) *q= *++p;
  if(!psinv(var,np)){ q=cp+np;
    for(p=var,s=cr,pp=par; s<pmax ;){
      for(*s=0.,r=cp; r<q ;) *s+= *p++ * *r++;
      (pp++)->cf += *s++;
     }
   }
  else ssq= -1.;
  free(cp); setdr(0); return ssq;
}

double fixtsf(struct fmod *x,int n,double *var,double *cr)
{ double *cp,*p,*q,*r,*s,*pmax;
  struct mcof *pp; double e,ssq,drfmod(struct fmod,double *);
  int j,k,psinv(double *,int);
  cp=(double *)calloc(np,sizeof(double));
  for(p=var,pmax=p+np*np; p<pmax ;) *p++ =0.;
  setdrf(1); pmax=cr+np;
  for(j=0,ssq=0.; j<n ;++j){
     e=drfmod(x[j],cr); ssq+=e*e;
     for(r=cp,s=cr,q=var,k=0; s<pmax ;++s,q+= ++k){
        *r++ +=e* *s;
        for(p=s; p<pmax ;) *q++ += *s * *p++; }
   }
  for(p=var,r=p+np*np,k=1; k<np ;p+= ++k)
     for(q=p+np; q<r ;q+=np) *q= *++p;
  if(!psinv(var,np)){ q=cp+np;
     if(ndif) oprj(var,np,nfc);
     for(p=var,s=cr,pp=pfc; s<pmax ;){ *s=0.;
        for(r=cp; r<q ;) *s+= *p++ * *r++;
        (pp++)->cf += *s++; } }
  else ssq= -1.;
  free(cp); setdrf(0); return ssq;
}


double parma(double *x,double *e)
{ struct mcof *p,*q; double y;
  for(y=0.,p=par,q=p+nar; p<q ;++p)
    y+= p->cf * *(x-(p->lag+1));
  for(p=pma,q=p+nma; p<q ;++p)
    y-= p->cf * *(e-(p->lag+1));
  *x=y; *e=0.;
  return y;
}


int resid(double *x,int n,int lag,double **pau,int nbin,
       double xa,double xb,int **phs,int *cks)
{ int j,m,*hist(double *,int,double,double,int,double *);
  double y,f,s,d,*autcor(double *,int,int),bin; 
  *pau=autcor(x,n,lag);
  *phs=hist(x,n,xa,xb,nbin,&bin);
  n=pwspec(x,n,0);
  m=n/2; f=2./n; s=m-1; s=sqrt(s);
  xa=1.02/s; xb=1.36/s; cks[0]=cks[1]=0;
  for(s=y=0.,j=0; j<m ;++j){
    s+=x[j]+x[j+1];
    if((d=fabs(s-(y+=f)))>xa){
      ++cks[0]; if(d>xb) ++cks[1];
     }
   }
  return n;
}

int sany(double *x,int n,double *pm,double *cd,double *ci,
       int nd,int ms,int lag)
{ struct complex *pc,*p,**qc,**q;
  int j,kk[16]; double *px,sd,si;
  *pm=xmean(x,n);
  if(nd){
    x[0]=sdiff(x[0],nd,0);
    for(j=1,px=x+1; j<n ;++j,++px)
      *px=sdiff(*px,nd,1);
    x+=nd; n-=nd;
   }
  n=pfac(n,kk,'e');
  pc=(struct complex *)calloc(n,sizeof(*pc));
  qc=(struct complex **)calloc(n,sizeof(*qc));
  fftgr(x,pc,n,kk,'d');
  for(j=0,p=pc; j<n ;++p)
    x[j++]=p->re*p->re+p->im*p->im;
  p=pc;
  if(ms){ smoo(x,n,ms);
    p->re=x[0]; p->im=1./x[0];
   }
  else{ sd=.5*(x[1]+x[n-1]);
    p->re=sd; p->im=1./sd;
   }
  for(j=1,++p; j<n ;++j,++p){
    p->re=x[j]; p->im=1./x[j];
   }
  fftgc(qc,pc,n,kk,'d');
  q=qc;
  sd=cd[0]=(*q)->re; si=ci[0]=(*q)->im;
  for(j=1,++q; j<=lag ;++j,++q){
    cd[j]=(*q)->re/sd; ci[j]=(*q)->im/si;
   }
  free(pc); free(qc);
  return n;
}


double sarma(double er)
{ struct mcof *p,*q; double y;
  for(y=er,p=par,q=p+nar; p<q ;++p)
    y+= p->cf * *(pz+(kst+p->lag)%max);
  for(p=pma,q=p+nma; p<q ;++p)
    y-= p->cf * *(pm+(kst+p->lag)%max);
  kst=(kst+max-1)%max; *(pm+kst)=er; *(pz+kst)=y;
  return y;
}
void setsim(int k)
{ int m;
  if(k){ kst=max=m=0;
    if(nar) max=(par+nar-1)->lag+1;
    if(nma && (m=(pma+nma-1)->lag+1)>max) max=m;
    pz=(double *)calloc(2*max,sizeof(double));
    pm=pz+max;
   }
  else free(pz);
}

double sintg(double y,int nd,int k)
{ double s;
  if(k==0) for(k=0; k<nd ;) f[k++]=0.;
  for(k=nd-1; k>=0 ;){
    s=f[k]; f[k--]+=y; y=s; }
  return f[0];
}

double seqts(double *x,int n,double *var,int kf)
{ double *pd,*pg,*pmax,*p,*q,*h,*f; int j;
  struct mcof *pp;
  double e,ssq,sig,sqrt(double),drmod(double,double *);
  pd=(double *)calloc(2*np,sizeof(double)); pg=pd+np;
  if(!kf){ for(p=var,pmax=p+np*np; p<pmax ;) *p++ =0.;
    for(p=var; p<pmax ;p+=np+1) *p=1.;
   }
  setdr(1); pmax=pg+np;
  for(j=0,ssq=0.; j<n ;){
    e=drmod(x[j++],pd); ssq+=e*e;
    for(p=pg,q=pd,f=var,sig=1.; p<pmax ;){
      for(*p=0.,h=pd; h<pg ;) *p+= *f++ * *h++;
      sig+= *p++ * *q++;
     }
    e/=(sig=sqrt(sig));
    for(p=pg,pp=par; p<pmax ;) (pp++)->cf +=e*(*p++/=sig);
    for(kf=0,p=pg,h=var; p<pmax ;++p,h+= ++kf)
      for(q=p,f=h; q<pmax ;f+=np) *f=(*h++ -= *p * *q++);
   }
  free(pd); setdr(0); return ssq;
}


double seqtsf(struct fmod *x,int n,double *var,int kf)
{ double *pd,*pg,*pmax,*p,*q,*h,*f; int i,j;
  struct mcof *pp;
  double e,ssq,sig,drfmod(struct fmod,double *);
  pd=(double *)calloc(2*np,sizeof(double)); pg=pd+np;
  if(kf==0){ e=1./nfc;
      for(p=var,i=0; i<np ;++i){
         for(j=0; j<np ;++j,++p){
            if(i==j) *p=1.; else *p=0.;
            if(ndif && (i<nfc && j<nfc)) *p-=e;} }
    }
  setdrf(1); pmax=pg+np;
  for(j=0,ssq=0.; j<n ;++j){
    e=drfmod(x[j],pd); ssq+=e*e;
    for(p=pg,q=pd,f=var,sig=1.; p<pmax ;){ *p=0.;
       for(h=pd; h<pg ;) *p+= *f++ * *h++;
       sig+= *p++ * *q++;
      }
    sig=sqrt(sig); e/=sig;
    for(p=pg,pp=pfc; p<pmax ;) (pp++)->cf+=e*(*p++/=sig);
    for(p=pg,h=var,kf=0; p<pmax ;++p,h+= ++kf)
      for(q=p,f=h; q<pmax ;f+=np) *f=(*h++ -= *p* *q++);
   }
  free(pd); setdrf(0); return ssq;
}


// Linear Systems solution routines
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_solv
  (JNIEnv *env, jobject obj, jdoubleArray da,  jdoubleArray  db,   jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *b = (double *)env->GetDoubleArrayElements( db, NULL);
	
	solv( a, b, n);
	
	env->ReleaseDoubleArrayElements(da, a, 0);
	env->ReleaseDoubleArrayElements(db, b, 0);
	
	return 1;
}
	
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_solvps
  (JNIEnv *env, jobject obj, jdoubleArray da,  jdoubleArray  db,   jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *b = (double *)env->GetDoubleArrayElements( db, NULL);
	
	solvps( a, b, n);
	
	env->ReleaseDoubleArrayElements(da, a, 0);
	env->ReleaseDoubleArrayElements(db, b, 0);
	
	return 1;
}
	
		
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_solvtd
  (JNIEnv *env, jobject obj, jdoubleArray da,  jdoubleArray  db,  jdoubleArray dc,  jdoubleArray dx,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *b = (double *)env->GetDoubleArrayElements( db, NULL);
	jdouble *c = (double *)env->GetDoubleArrayElements( dc, NULL);
	jdouble *x = (double *)env->GetDoubleArrayElements( dx, NULL);
	
	solvtd( a, b, c, x, n);
	
	env->ReleaseDoubleArrayElements(da, a, 0);
	env->ReleaseDoubleArrayElements(db, b, 0);
	env->ReleaseDoubleArrayElements(dc, c, 0);
	env->ReleaseDoubleArrayElements(dx, x, 0);
	
	return 1;
}
	
			
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_solvru
  (JNIEnv *env, jobject obj, jdoubleArray da,  jdoubleArray  db,   jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *b = (double *)env->GetDoubleArrayElements( db, NULL);
	
	solvru( a, b, n);
	
	env->ReleaseDoubleArrayElements(da, a, 0);
	env->ReleaseDoubleArrayElements(db, b, 0);
	
	return 1;
}

// SVD routines	
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_svduv
  (JNIEnv *env, jobject obj, jdoubleArray dd, jdoubleArray da, jdoubleArray du, jint m,   jdoubleArray dv, jint n) 
 {
	jdouble *d = (double *)env->GetDoubleArrayElements( dd, NULL);
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *u = (double *)env->GetDoubleArrayElements( du, NULL);
	jdouble *v = (double *)env->GetDoubleArrayElements( dv, NULL);
		

   svduv(d, a, u,  m, v, n); 
   
	env->ReleaseDoubleArrayElements(dd, d, 0);
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements(du, u, 0);
	env->ReleaseDoubleArrayElements(dv, v, 0);

 return 1;
	
}



extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_sv2u1v
  (JNIEnv *env, jobject obj, jdoubleArray dd, jdoubleArray da,  jint m,   jdoubleArray dv, jint n) 
 {
	jdouble *d = (double *)env->GetDoubleArrayElements( dd, NULL);
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *v = (double *)env->GetDoubleArrayElements( dv, NULL);
		

    sv2u1v(d, a, m, v, n); 
   
	env->ReleaseDoubleArrayElements(dd, d, 0);
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements(dv, v, 0);

 return 1;
	
}
 	
	


extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_sv2uv
  (JNIEnv *env, jobject obj, jdoubleArray dd, jdoubleArray da,  jdoubleArray du,  jint m,   jdoubleArray dv, jint n) 
 {
	jdouble *d = (double *)env->GetDoubleArrayElements( dd, NULL);
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *u = (double *)env->GetDoubleArrayElements( du, NULL);
	jdouble *v = (double *)env->GetDoubleArrayElements( dv, NULL);
		
    sv2uv(d, a, u, m, v, n); 
   
	env->ReleaseDoubleArrayElements(dd, d, 0);
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements(du, u, 0);
	env->ReleaseDoubleArrayElements(dv, v, 0);

 return 1;
	
}
 	
	

extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_sv2val
  (JNIEnv *env, jobject obj, jdoubleArray dd, jdoubleArray da,  jint m,    jint n) 
 {
	jdouble *d = (double *)env->GetDoubleArrayElements( dd, NULL);
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	
    sv2val(d, a, m, n); 
   
	env->ReleaseDoubleArrayElements( dd, d, 0);
	env->ReleaseDoubleArrayElements( da, a, 0);
	
 return 1;
	
}


extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_svdval
  (JNIEnv *env, jobject obj, jdoubleArray dd, jdoubleArray da,  jint m,    jint n) 
 {
	jdouble *d = (double *)env->GetDoubleArrayElements( dd, NULL);
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	
    svdval(d, a, m, n); 
   
	env->ReleaseDoubleArrayElements( dd, d, 0);
	env->ReleaseDoubleArrayElements( da, a, 0);
	
 return 1;
	
}

// matrix inversion routines
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_minv
  (JNIEnv *env, jobject obj, jdoubleArray da,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	
    minv(a, n); 
   
	env->ReleaseDoubleArrayElements( da, a, 0);
	
 return 1;
	
}


extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_psinv
  (JNIEnv *env, jobject obj, jdoubleArray dv,  jint n) 
 {
	jdouble *v = (double *)env->GetDoubleArrayElements( dv, NULL);
	
    psinv(v, n);    
	
	env->ReleaseDoubleArrayElements( dv, v, 0);
	
 return 1;
	
}


extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_ruinv
  (JNIEnv *env, jobject obj, jdoubleArray da,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	
    ruinv(a, n);    
	
	env->ReleaseDoubleArrayElements( da, a, 0);
	
 return 1;
	
}

// Eigenanalysis routines
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_eigval
  (JNIEnv *env, jobject obj, jdoubleArray da, jdoubleArray dev,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *ev = (double *)env->GetDoubleArrayElements( dev, NULL);
	
    eigval(a, ev,  n); 
   
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements( dev, ev, 0);
	
 return 1;
	
}

extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_eigen
  (JNIEnv *env, jobject obj, jdoubleArray da, jdoubleArray dev,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *ev = (double *)env->GetDoubleArrayElements( dev, NULL);
	
    eigen(a, ev,  n); 
   
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements( dev, ev, 0);
	
 return 1;
	
}

 
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_evmax
  (JNIEnv *env, jobject obj, jdoubleArray da, jdoubleArray du,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *u = (double *)env->GetDoubleArrayElements( du, NULL);
	
    evmax(a,  u,  n); 
   
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements( du,  u, 0);
	
 return 1;
	
}
	
	
// matrix generation routines

extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_smgen
  (JNIEnv *env, jobject obj, jdoubleArray da, jdoubleArray deval, jdoubleArray devec,  jint n) 
 {
	jdouble *a = (double *)env->GetDoubleArrayElements( da, NULL);
	jdouble *eval = (double *)env->GetDoubleArrayElements( deval, NULL);
	jdouble *evec = (double *)env->GetDoubleArrayElements( devec, NULL );
	
    smgen(a, eval,  evec, n); 
   
	env->ReleaseDoubleArrayElements( da, a, 0);
	env->ReleaseDoubleArrayElements( deval, eval, 0);
	env->ReleaseDoubleArrayElements( devec, evec, 0);
	
 return 1;
	
}
	
	
	
extern "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_ortho
  (JNIEnv *env, jobject obj, jdoubleArray de,  jint n) 
 {
	jdouble *e = (double *)env->GetDoubleArrayElements( de, NULL);
	
    ortho(e,  n); 
   
	env->ReleaseDoubleArrayElements( de, e, 0);
	
 return 1;
	
}


/*
     Factor an integer into its prime factors.

     int pfac(int n,int *kk,int fe)
        n = input integer
        kk = pointer to array containing factors of n, with
               kk[0] = number of factors and the output returned
                       n' = kk[1]*kk[2]* -- *kk[kk[0]]
               (The dimension of kk should be 32.)
        fe = control flag, with:
               fe = 'e' -> even integer output required
               fe = 'o' -> odd integers allowed
       return value: n' = integer factored (n' <= n)
	
	int n=pfac(n,kk,'e');
	*/
	/*
     void fftgr(double *x,Cpx *ft,int n,int *kk,int inv)
        x = pointer to array of real input series (dimension = n)
        ft = pointer to complex structure array of Fourier transform
             output
        n = length of input and output series
        kk = pointer to array of factors of n (see pfac below)
        inv = control flag, with:
                inv='d' -> direct transform
                inv!='d' -> inverse transform
*/
extern "C"	
JNIEXPORT jint JNICALL Java_CCOps_CCOps_ccfft
  (JNIEnv *env, jobject obj, jdoubleArray div, jdoubleArray doutRe, jdoubleArray doutIm, jint N) {
  
  jdouble *iv = (double *)env->GetDoubleArrayElements( div, NULL);
  jdouble *outRe = (double *)env->GetDoubleArrayElements( doutRe, NULL);	
  jdouble *outIm = (double *)env->GetDoubleArrayElements( doutIm, NULL);
	
	int kk[32];

 struct complex *ft = (struct complex *) calloc(N, sizeof(*ft));
 struct complex *f;
 N = pfac(N,kk,'o');
 fftgr(iv, ft,N, kk, 'd');
 int k=0;
 for (f = ft;  k < N;  ++f, ++k) {
     outRe[k] = f->re;
	 outIm[k] = f->im;
	 }
	 
	env->ReleaseDoubleArrayElements( div, iv, 0);
	env->ReleaseDoubleArrayElements( doutRe, outRe, 0);
	env->ReleaseDoubleArrayElements( doutIm, outIm, 0);
	
	free(ft);
	return 1;
	}
			
/*
     Compute the power spectrum of a series.
     int pwspec(double *x,int n,int m)
       x = pointer to array containing input/output series
           (converted to a power spectra at exit)
       n = number of points in the series
       m = control flag specifying order of smoothing, with:
             m=0 -> no smoothing
             m>0 -> smooth output power spectra, using
                     an order m average (see smoo)
      return value: n = size of series used to compute power spectra
                        (n <= input n, even values required)
          The output power spectra is defined by
           ps(w[j]) = | ft[j] |^2 / <x^2>  ,  where
           <x^2> = { Sum(j=0 to n-1) x[j]^2 }/n .
          This normalization yields  Sum(j=0 to n-1) ps(w[j]) = 1  .
*/																				
extern  "C"
JNIEXPORT jint JNICALL Java_CCOps_CCOps_pwspec
  (JNIEnv *env, jobject obj, jdoubleArray div, jint N, jint M) {
 jdouble *iv = (double *)env->GetDoubleArrayElements(div, NULL);
 
  pwspec(iv, N, M);
  env->ReleaseDoubleArrayElements(div, iv, 0);
  return 1;
  }
  

// compute autocorrelation
extern  "C"
JNIEXPORT void JNICALL Java_CCOps_CCOps_ccautcor( JNIEnv *env, jobject obj, 
   jdoubleArray x, jint N, jdoubleArray res, jint lag) {

  jdouble *ix = (double *) env->GetDoubleArrayElements(x, NULL);
  jdouble *ires = (double *) env->GetDoubleArrayElements(res, NULL);
  
  double *rc = autcor(ix, N, lag);

  for (int n=1; n<=lag; n++)  ires[n-1] = rc[n];

  env->ReleaseDoubleArrayElements(x, ix, 0);
  env->ReleaseDoubleArrayElements(res, ires, 0);
  }


  
/*qrlsq

     Compute a linear least squares solution for A*x = b
     using a QR reduction of the matrix A.

     double qrlsq(double *a,double *b,int m,int n,int *f)
       a = pointer to m by n design matrix array A
           This is altered to upper right triangular
           form by the computation.
       b = pointer to array of measurement values b
           The first n components of b are overloaded
           by the solution vector x.
       m = number of measurements (dim(b)=m)
       n = number of least squares parameters (dim(x)=n)
           (dim(a)=m*n)
       f = pointer to store of status flag, with
              *f = 0  -> solution valid
              *f = -1 -> rank of A < n (no solution)
      return value: ssq = sum of squared fit residuals


     The QR algorithm employs an orthogonal transformation to reduce
     the matrix A to upper right triangular form, with

          A = Q*R  and  R = Q~*A .

     The matrix R has non-zero components confined to the range
     0 <= i <= j < n.  The system vector b is transformed to

          b' = U~*b  and  Sum(k=j to n-1) R[j,k]*x[k] = b'*[j]

     is solved for x, using 'solvru' (see Chapter 1). The sum
     of squared residuals is

          ssq = Sum(i=n to m-1){b~[i]^2} .
*/

  JNIEXPORT jdouble JNICALL Java_CCOps_CCOps_qrlsq
  (JNIEnv *env, jobject obj, jdoubleArray da, jdoubleArray db, jint m, jint n, jintArray df) {
  
  double *a = (double *)env->GetDoubleArrayElements( da, NULL);
  double *b = (double *)env->GetDoubleArrayElements( db, NULL);
  int *f = (int *)env->GetIntArrayElements( df, NULL);
  
  double dv = qrlsq( a, b, m, n, f);

  env->ReleaseDoubleArrayElements( da, a, 0);
  env->ReleaseDoubleArrayElements( db, b, 0);
  env->ReleaseIntArrayElements( df, (jint *)f, 0);
  return dv;
	
}


static int N1, N2, N3;  // A  is N1XN2, B is N2XN3
double *AA, *BB, *CC;  // pointer to matrices A, B, C
int NumThreads = 20;  // number of threads to use
int SliceRows;   // how many rows each slice has


// multiply a slice of the matrix consisted from rows sliceStart up to sliceEnd
void multiplySlice( int sliceStart, int sliceEnd, int threadId)  {
	double *pA;
	double *pB;
	double *pC;
	double smrowcol;
  
  if (sliceStart >= N1) return;
  if (sliceEnd >= N1) sliceEnd = N1;
  
  pC = CC + threadId*SliceRows*N3;  // position that this thread starts outputting results to C matrix
  // for all the rows of the matrix A that the thread has the responsibility to compute
  for (int i=sliceStart; i<sliceEnd; i++) {
 for (int j=0; j<N3; j++) {

	  smrowcol = 0.0;
	  pA = AA + i*N2;  // current row start
	  pB = BB + j*N2;
	  
	  for (int k=0; k<N2; k++) {
		smrowcol += *pA * *pB;
		pA++;  
		//pB += n3;  // normally this advances to the next column element of B, but it's transposed!
		pB++;  // matrix B enters the routine transposed to exploit cache locality
		}
		*pC++ = smrowcol;
	}
  }
 }

 // the thread function
void * threadMultiply( void * slice) {
  long sl = (long) slice; // which slice of the matrix belongs to that thread
  int s = (int) sl;
  
	multiplySlice( SliceRows*s,  SliceRows*s+SliceRows, s);  // each thread multiplies its part of rows
	
  return (int *)1;
}


  

  
extern "C"
JNIEXPORT void JNICALL Java_CCOps_CCOps_pt
 (JNIEnv *env, jobject obj, jdoubleArray a, jint n1, jint n2, 
	jdoubleArray b, jint n3, jdoubleArray c) {
	AA = (double *)env->GetDoubleArrayElements( a, NULL);
	BB = (double *)env->GetDoubleArrayElements( b, NULL);
	CC = (double *)env->GetDoubleArrayElements( c, NULL);
		
	double *pA;
	double *pB;
	double *pC;
	double smrowcol;
  
  // keep these parameters globally since they are needed by all threads
   N1 = n1;
   N2 = n2;
   N3 = n3;
  
   SliceRows = (int)(N1/NumThreads);  // number of rows to process each thread
   
   if (SliceRows==0)  // a very small matrix: multiply serially
      multiplySlice(0, N1, 0);
	else {
   // allocate memory for the threads. One more thread is required to process the last part of the matrix
   pthread_t * thread;
   thread = (pthread_t*) malloc((NumThreads+1)*sizeof(pthread_t));
   
   // wait for the threads to finish
   for (int i = 0; i <= NumThreads; i++) {
    if (pthread_create(&thread[i], NULL, threadMultiply, (void *)i) != 0)
	{
	perror("Can't create thread");
	free(thread);
	return;
	 }
	}
	
   for (int i=0; i<= NumThreads; i++)
     pthread_join(thread[i], NULL);
	 
	 free(thread);
	}
	
	env->ReleaseDoubleArrayElements(a, AA, 0);
	env->ReleaseDoubleArrayElements( b, BB, 0);
	env->ReleaseDoubleArrayElements(c, CC, 0);
	
	
}
