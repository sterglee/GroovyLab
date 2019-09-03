
class Quicksort {

    static void swap(int[] a, int i, int j) {
        def temp = a[i]
        a[i] = a[j]
        a[j] = temp
    }

    static void quicksort(int[] a, int L, int R) {
        int m = a[(L+R) >> 1]
        int i=L
        int j=R
        while (i<=j) {
            while (a[i]<m) i++
            while (a[j]>m) j--
            if (i<=j) {
                swap(a, i, j)
                i++
                j--
            }
        }
        if (L<j) quicksort(a,L,j)
        if (R>i) quicksort(a,i,R)
    }

    static void quicksort(int[] a) {
        quicksort(a, 0, a.length-1)
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // Sample data
        int[] a = new int[100000]
        for (i in 0..<a.length) {
            a[i] = ((i*3) >> 1)+1
            if (i%3==0) a[i] = -a[i]
        }

        quicksort(a)

        long total = System.currentTimeMillis() - start;
        System.out.println("[Quicksort-${System.getProperty("project.name")} Benchmark Result: " + total + "]");
    }
}

