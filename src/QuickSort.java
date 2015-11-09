/**
 * Created by andreahe on 4/28/15.
 */
public class QuickSort {
    /**
     * Quicksort algorithm.
     * @param a an array of Comparable items.
     */
    public static <AnyType extends Comparable<? super AnyType>> void quicksort( AnyType [ ] a )
    {
        quicksort( a, 0, a.length - 1 );
    }

    private static final int CUTOFF = 3;

    /**
     * Method to swap to elements in an array.
     * @param a an array of objects.
     * @param index1 the index of the first object.
     * @param index2 the index of the second object.
     */
    public static <AnyType> void swapReferences( AnyType [ ] a, int index1, int index2 )
    {
        AnyType tmp = a[ index1 ];
        a[ index1 ] = a[ index2 ];
        a[ index2 ] = tmp;
    }

    /**
     * Return median of left, center, and right.
     * Order these and hide the pivot.
     */
    private static <AnyType extends Comparable<? super AnyType>>
    AnyType median3( AnyType [ ] a, int left, int right )
    {
        int center = ( left + right ) / 2;
        if( a[ center ].compareTo( a[ left ] ) < 0 )
            swapReferences( a, left, center );
        if( a[ right ].compareTo( a[ left ] ) < 0 )
            swapReferences( a, left, right );
        if( a[ right ].compareTo( a[ center ] ) < 0 )
            swapReferences( a, center, right );

        // Place pivot at position right - 1
        swapReferences( a, center, right - 1 );
        return a[ right - 1 ];
    }

    /**
     * Internal quicksort method that makes recursive calls.
     * Uses median-of-three partitioning and a cutoff of 10.
     * @param a an array of Comparable items.
     * @param left the left-most index of the subarray.
     * @param right the right-most index of the subarray.
     */
    private static <AnyType extends Comparable<? super AnyType>> void quicksort( AnyType [ ] a, int left, int right )
    {
        if( left + CUTOFF <= right )
        {
            AnyType pivot = median3( a, left, right );

            // Begin partitioning
            int i = left, j = right - 1;
            for( ; ; )
            {
                while( a[ ++i ].compareTo( pivot ) < 0 ) { }
                while( a[ --j ].compareTo( pivot ) > 0 ) { }
                if( i < j )
                    swapReferences( a, i, j );
                else
                    break;
            }

            swapReferences( a, i, right - 1 );   // Restore pivot

            quicksort( a, left, i - 1 );    // Sort small elements
            quicksort( a, i + 1, right );   // Sort large elements
        }
        else  // Do an insertion sort on the subarray
            insertionSort( a, left, right );
    }

    /**
     * Internal insertion sort routine for subarrays
     * that is used by quicksort.
     * @param a an array of Comparable items.
     * @param left the left-most index of the subarray.
     * @param right the right-most index of the subarray.
     */
    private static <AnyType extends Comparable<? super AnyType>> void insertionSort( AnyType [ ] a, int left, int right )
    {
        for( int p = left + 1; p <= right; p++ )
        {
            AnyType tmp = a[ p ];
            int j;

            for( j = p; j > left && tmp.compareTo( a[ j - 1 ] ) < 0; j-- )
                a[ j ] = a[ j - 1 ];
            a[ j ] = tmp;
        }
    }
}
