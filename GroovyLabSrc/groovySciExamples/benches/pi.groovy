
class PiDigits {
static final int L = 10

static void main(String[] args) {
def start = System.currentTimeMillis()
def n = 10000;

if (args.length >= 1)
n = Integer.parseInt(args[0])

int j = 0

PiDigitSpigot digits = new PiDigitSpigot()

while (n > 0) {
if (n >= L) {
for (int i = 0; i < L; i++)
digits.next()
// print digits.next()
j += L
} else {
for (int i = 0; i < n; i++)
digits.next()
// print digits.next()
//
// for (int i = n; i < L; i++)
// print " "
j += n
}
// print "\t:"
// println j
n -= L
}

def total = System.currentTimeMillis() - start
        System.out.println("[PiDigits-" + System.getProperty("project.name")+ " Benchmark Result: " + total + "]");
}

static class PiDigitSpigot {
Transformation z, x, inverse

PiDigitSpigot() {
z = new Transformation(1, 0, 0, 1)
x = new Transformation(0, 0, 0, 0)
inverse = new Transformation(0, 0, 0, 0)
}

int next() {
def y = digit()
if (isSafe(y)) {
z = produce(y)
return y
} else {
z = consume(x.next())
return next()
}
}

int digit() {
z.extract(3)
}

boolean isSafe(int digit) {
digit == z.extract(4)
}

Transformation produce(int i) {
(inverse.qrst(10, -10 * i, 0, 1)).compose(z)
}

Transformation consume(Transformation a) {
z.compose(a)
}
}


static class Transformation {
BigInteger q, r, s, t
int k

Transformation(int q, int r, int s, int t) {
this.q = BigInteger.valueOf(q)
this.r = BigInteger.valueOf(r)
this.s = BigInteger.valueOf(s)
this.t = BigInteger.valueOf(t)
k = 0
}

Transformation(BigInteger q, BigInteger r, BigInteger s, BigInteger t) {
this.q = q
this.r = r
this.s = s
this.t = t
k = 0
}

public Transformation next() {
k++
q = BigInteger.valueOf(k)
r = BigInteger.valueOf(4 * k + 2)
s = BigInteger.valueOf(0)
t = BigInteger.valueOf(2 * k + 1)
this
}

int extract(int j) {
BigInteger bigj = BigInteger.valueOf(j)
BigInteger numerator = (q.multiply(bigj)).add(r)
BigInteger denominator = (s.multiply(bigj)).add(t)
return (numerator.divide(denominator)).intValue()
}

Transformation qrst(int q, int r, int s, int t) {
this.q = BigInteger.valueOf(q)
this.r = BigInteger.valueOf(r)
this.s = BigInteger.valueOf(s)
this.t = BigInteger.valueOf(t)
k = 0
this
}

Transformation compose(Transformation a) {
new Transformation(
q.multiply(a.q),
(q.multiply(a.r)).add((r.multiply(a.t))),
(s.multiply(a.q)).add((t.multiply(a.s))),
(s.multiply(a.r)).add((t.multiply(a.t)))
)
}
}
}



