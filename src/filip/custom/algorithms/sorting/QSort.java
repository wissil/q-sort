package filip.custom.algorithms.sorting;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * A class that represents an implementation of a <b>Quick Sort</b> algorithm.<br>
 * 
 * This algorithm operates in <code>O(nlogn)</code> time complexity, 
 * and <code>O(n)</code> space complexity, where <code>n</code> is the number of elements to be sorted.
 * 
 * @author fiilip
 *
 */
public class QSort {
	
	/**
	 * A class that represents a single-threaded job of a <b>Quick Sort</b> algorithm.<br>
	 * Generally, a single-threaded job is considered to be sorting a single sub-array.
	 * 
	 * @author fiilip
	 *
	 */
	private static class QSortJob<T extends Comparable<T>> extends RecursiveAction {
		
		/**
		 * Generated serial Id.
		 */
		private static final long serialVersionUID = -3570175226308337131L;
		
		/**
		 * Size of the almost-sorted array on which the insertion sort is executed.<br>
		 * 
		 * This prunes quick sort recursion tree by executing a different sorting algorithm 
		 * on a partition that sits too deep.
		 */
		private static final int INSERTION_CUTOFF = 150;
		
		/**
		 * Treshold on the sub-array size for when the algorithm falls back from 
		 * parallel to sequential computation.<br>
		 * 
		 * This is used to spare the time of creating new threads vs. the actual computation time.
		 */
		private static final int SEQUENTIAL_TRESHOLD = 1_000;
		
		/**
		 * Array of data to be sorted.
		 */
		private final T[] arr;
		
		/**
		 * Left boundary.
		 */
		private final int left;
		
		/**
		 * Right boundary.
		 */
		private final int right;
		
		/**
		 * Creates a new instance of {@link QSortJob}.
		 * 
		 * @param arr Array of data to be sorted.
		 * @param left Left boundary.
		 * @param right Right boundary.
		 */
		private QSortJob(T[] arr, int left, int right) {
			this.arr = Objects.requireNonNull(arr);
			this.left = left;
			this.right = right;
		}

		@Override
		protected void compute() {
			boolean doInParallel = right - left + 1 >= SEQUENTIAL_TRESHOLD;
			
			if (right - left + 1 > INSERTION_CUTOFF) {
				quickSort(doInParallel);
			} else {
				// Insertion Sort fallback
				insertionSort();
			}
		}

		/**
		 * Performs the insertion sort algorithm on the current sub-array.
		 */
		private void insertionSort() {
			int i = left + 1;
			
			while (i <= right) {
				int j = i;
				
				while (j > 0 && arr[j-1].compareTo(arr[j]) > 0) {
					swap(j, j-1);
					j --;
				}		
				i ++;
			}
		}

		/**
		 * Performs the quicksort algorithm on the current sub-array.
		 */
		private void quickSort(boolean doInParallel) {
			if (doInParallel) {
				// parallel sort
				pQsort();
			} else {
				// sequential sort
				sQsort(left, right);
			}
		}

		/**
		 * Sequentially sorts the current sub-array.
		 * 
		 * @param from Index to sort from.
		 * @param to Index to sort to.
		 */
		private void sQsort(int from, int to) {
			if (from >= to) return;
			
			T pivotValue = arr[from];
			 
	        swap(from, to);
	 
	        int storeIndex = from;
	        for (int i=from; i<to; i++) {
	            if (arr[i].compareTo(pivotValue) < 0) {
	                swap(i, storeIndex);
	                storeIndex++;
	            }
	        }
	 
	        swap(storeIndex, to);

			sQsort(from, storeIndex);
			sQsort(storeIndex + 1, to);
		}

		/**
		 * Performs the parallel quicksort.
		 */
		private void pQsort() {
			int pivotIndex = left + (right - left) / 2;
			pivotIndex = partition(pivotIndex);

			invokeAll(
					new QSortJob<>(arr, left, pivotIndex-1),
					new QSortJob<>(arr, pivotIndex+1, right));
		}

		/**
		 * Partitions the array around the given <code>pivot</code>. <br>
		 * 
		 * The array is partitioned in a way that left all the elements in the left sub-array
		 * are less than the <code>pivot</code>, and all the elements in the right sub-array
		 * are greater than the <code>pivot</code>.
		 * 
		 * @param pivotIndex Index of the pivot around which the array is being partitioned.
		 * @return Newly calculated store index.
		 */
		private int partition(int pivotIndex) {
			T pivotValue = arr[pivotIndex];
			 
	        swap(pivotIndex, right);
	 
	        int storeIndex = left;
	        for (int i=left; i<right; i++) {
	            if (arr[i].compareTo(pivotValue) < 0) {
	                swap(i, storeIndex);
	                storeIndex++;
	            }
	        }
	 
	        swap(storeIndex, right);
	 
	        return storeIndex;
		}

		/**
		 * Swaps the given elements <code>i</code> and <code>j</code>
		 * in the array.
		 * 
		 * @param i Element to be swaped with <code>j</code>.
		 * @param j Element to be swaped with <code>i</code>.
		 */
		private void swap(int i, int j) {
			T tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}	
	}

	/**
	 * Sorts the given array <code>arr</code>.
	 * 
	 * @param arr Array to be sorted.
	 */
	public static <T extends Comparable<T>> void sort(T[] arr) {
		ForkJoinPool.commonPool().invoke(new QSortJob<>(arr, 0, arr.length-1));
	}
	
	/**
	 * Checks whether the given array is sorted in it's natural order.
	 * 
	 * @param arr Array of interest.
	 * @return <code>true</code> if the array is sorted, and <code>false</code> otherwise.
	 */
	public static <T extends Comparable<T>> boolean isSorted(T[] arr) {
		for (int i=0, n=arr.length-1; i<n; i++) {
			if (arr[i].compareTo(arr[i+1]) > 0) return false;
		}
		return true;
	}
}
