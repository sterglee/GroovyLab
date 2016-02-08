
class MyClass {
	def comp(int N) {
		double sm = 0.0
		for (k in 0..100000)
			sm += (k-0.989*k)/(2000000.3*k)
		return sm
	}
}
return MyClass
