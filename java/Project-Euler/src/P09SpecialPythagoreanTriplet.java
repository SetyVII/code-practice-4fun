public class P09SpecialPythagoreanTriplet {
    // Probably need less complex I guess, it's now 0n3
    static void main() {
            // O(n³)
            int rpta = 0;
            int limit = 1000;
            for (int i = 1;i<limit;i++){
                for (int j = i; j < limit; j++) {
                    for (int k = j; k < limit; k++) {
                        if(i*i+j*j==k*k){
                            System.out.printf("This number is pitagorean triplet: %d < %d < %d \n",i,j,k);
                            if(i+j+k==1000){
                                rpta = i*j*k;
                            }
                        }
                    }
                }
            }
            System.out.println("RPTA = "+rpta);
    }
}
