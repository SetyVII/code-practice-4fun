import java.util.ArrayList;
import java.util.List;

public class P11LargestProductInAGrid {

    public static void main(String[] args) {

            int[][] matrix = {
                    {8, 2, 22, 97, 38, 15, 0, 40, 0, 75, 4, 5, 7, 78, 52, 12, 50, 77, 91, 8},
                    {49, 49, 99, 40, 17, 81, 18, 57, 60, 87, 17, 40, 98, 43, 69, 48, 4, 56, 62, 0},
                    {81, 49, 31, 73, 55, 79, 14, 29, 93, 71, 40, 67, 53, 88, 30, 3, 49, 13, 36, 65},
                    {52, 70, 95, 23, 4, 60, 11, 42, 69, 24, 68, 56, 1, 32, 56, 71, 37, 2, 36, 91},
                    {22, 31, 16, 71, 51, 67, 63, 89, 41, 92, 36, 54, 22, 40, 40, 28, 66, 33, 13, 80},
                    {24, 47, 32, 60, 99, 3, 45, 2, 44, 75, 33, 53, 78, 36, 84, 20, 35, 17, 12, 50},
                    {32, 98, 81, 28, 64, 23, 67, 10, 26, 38, 40, 67, 59, 54, 70, 66, 18, 38, 64, 70},
                    {67, 26, 20, 68, 2, 62, 12, 20, 95, 63, 94, 39, 63, 8, 40, 91, 66, 49, 94, 21},
                    {24, 55, 58, 5, 66, 73, 99, 26, 97, 17, 78, 78, 96, 83, 14, 88, 34, 89, 63, 72},
                    {21, 36, 23, 9, 75, 0, 76, 44, 20, 45, 35, 14, 0, 61, 33, 97, 34, 31, 33, 95},
                    {78, 17, 53, 28, 22, 75, 31, 67, 15, 94, 3, 80, 4, 62, 16, 14, 9, 53, 56, 92},
                    {16, 39, 5, 42, 96, 35, 31, 47, 55, 58, 88, 24, 0, 17, 54, 24, 36, 29, 85, 57},
                    {86, 56, 0, 48, 35, 71, 89, 7, 5, 44, 44, 37, 44, 60, 21, 58, 51, 54, 17, 58},
                    {19, 80, 81, 68, 5, 94, 47, 69, 28, 73, 92, 13, 86, 52, 17, 77, 4, 89, 55, 40},
                    {4, 52, 8, 83, 97, 35, 99, 16, 7, 97, 57, 32, 16, 26, 26, 79, 33, 27, 98, 66},
                    {88, 36, 68, 87, 57, 62, 20, 72, 3, 46, 33, 67, 46, 55, 12, 32, 63, 93, 53, 69},
                    {4, 42, 16, 73, 38, 25, 39, 11, 24, 94, 72, 18, 8, 46, 29, 32, 40, 62, 76, 36},
                    {20, 69, 36, 41, 72, 30, 23, 88, 34, 62, 99, 69, 82, 67, 59, 85, 74, 4, 36, 16},
                    {20, 73, 35, 29, 78, 31, 90, 1, 74, 31, 49, 71, 48, 86, 81, 16, 23, 57, 5, 54},
                    {1, 70, 54, 71, 83, 51, 54, 69, 16, 92, 33, 48, 61, 43, 52, 1, 89, 19, 67, 48}
            };
            int length = matrix[0].length;
            int [][] tranpose = new int[length][matrix.length];
            List<Integer> list = new ArrayList<>();
            // Horizontal
            for(int row = 0; row<matrix.length; row++){

                for(int col = 0; col<matrix[row].length;col++){
                    int number = matrix[row][col];
                    // here I cover all the number so...



                    // end
                    tranpose[col][row] = number;
                    // System.out.printf("Int the position [%d] [%d] is store the value: %d \n",row,col,number);

                    // check a number is the correct position
                    if(matrix[row].length-(col)<4){
                        // System.out.println("Can store is the limit for 4 adjacent numbers");
                    }else{
                        int p = 1;
                        for (int i = 0; i < 4; i++) {
                            p*=matrix[row][col+i];
                        }
                        list.add(p);
                    }
                }
            }
            System.out.println("-----–-----------");
            for (int i = 0; i < tranpose.length; i++) {
                for (int j = 0; j < tranpose[i].length; j++) {
                    int notNumber = tranpose[i][j];
                    // System.out.printf("YO the position [%d] [%d] is store the value: %d \n",i,j,notNumber);
                    if(tranpose[i].length-(j)<4){
                        // System.out.println("Cant store is the limit for 4 adjacent numbers");
                    }else{
                        int p = 1;
                        for (int l = 0; l < 4; l++) {
                            p*=tranpose[i][j+l];
                        }
                        list.add(p);
                    }
                }
            }
            // here give al diagonal position

            for (int fila = 0; fila < matrix.length; fila++) {

                // Starts from fila so we only traverse one half of the matrix
                for (int columna = fila; columna < matrix[fila].length; columna++) {

                    // Need 4 rows and 4 columns available
                    if (matrix.length - fila < 4 ||
                            matrix[fila].length - columna < 4) {

                        continue;
                    }

                    // Original matrix product
                    int p = 1;

                    // Reflected matrix product
                    int n = 1;

                    for (int i = 0; i < 4; i++) {

                        int filaR = fila + i;
                        int columnaR = columna + i;

                        // Original diagonal
                        System.out.printf(
                                "Original: [%d][%d] = %d%n",
                                filaR,
                                columnaR,
                                matrix[filaR][columnaR]
                        );

                        p *= matrix[filaR][columnaR];


                        // If fila == columna we are on the main diagonal.
                        // Its reflection is exactly the same diagonal.
                        if (fila != columna) {

                            System.out.printf(
                                    "Reflected: [%d][%d] = %d%n",
                                    columnaR,
                                    filaR,
                                    tranpose[filaR][columnaR]
                            );

                            n *= tranpose[filaR][columnaR];
                        }
                    }

                    // Original product
                    list.add(p);

                    // Only add reflected product if it is actually different
                    if (fila != columna) {
                        list.add(n);
                    }
                }
            }
            for (int fila = 0; fila < matrix.length; fila++) {

                for (int columna = matrix[fila].length - 1;
                     columna >= 0;
                     columna--) {

                    if (matrix.length - fila < 4 ||
                            columna + 1 < 4) {

                        continue;
                    }

                    int p = 1;

                    for (int i = 0; i < 4; i++) {
                        p *= matrix[fila + i][columna - i];
                    }

                    list.add(p);
                }
            }


            //for(Integer n : list) System.out.println(n);
            int rpta = list.stream().max(Integer::compareTo).get();
            System.out.println("for only vertical and horizontal the max value is :"+rpta);
        }
    }


