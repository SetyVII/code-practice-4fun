public class P10SummationOfPrimes
{
    static boolean isPrime(long n){
        int solve = 0;
        for(int i = 1; i<=n/2;i++){
            if(n%i==0){
                solve++;
            }
            if(solve>1){
                return false;
            }
        }
        return true;
    }

    static void main() {
//        // Take to long to do a good job
//        List<Long> l = new ArrayList<>();
//        long sum = 0;
//        for (long i = 3; i < 2000000; i++) {
//            if(i%2!=0){
//                boolean check = isPrime(i);
//                if(check){
//                    l.add(i);
//                    System.out.println("this number is Prime" + i);
//                }
//            }
//
//        }
//        System.out.println(sum+2);

        // Best approach for Sieve of Eratosthenes
        boolean[] sieve = new boolean[2000000];

        for(int i = 2; i < sieve.length; i++) {sieve[i] = true;};
        for(int i = 2; Math.pow(i,2) < sieve.length; i++)
            if(sieve[i])
                for(int j = (int) Math.pow(i,2); j < sieve.length; j += i) sieve[j] = false;

        long sum = 0;
        for(int i = 2; i < sieve.length; i++)
            if(sieve[i]) sum += i;
        System.out.println(sum);
    }
}


