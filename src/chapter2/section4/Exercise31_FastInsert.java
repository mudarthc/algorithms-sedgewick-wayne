package chapter2.section4;

import edu.princeton.cs.algs4.StdOut;
import util.ArrayUtil;

/**
 * Created by Rene Argento on 26/03/17.
 */
@SuppressWarnings("unchecked")
public class Exercise31_FastInsert {

    private class PriorityQueueFastInsert<Key extends Comparable<Key>> {

        private Key[] priorityQueue;
        private int size = 0; // in priorityQueue[1..n] with pq[0] unused

        private int numberOfCompares;

        PriorityQueueFastInsert() {
            priorityQueue = (Key[]) new Comparable[2];
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        public void insert(Key key) {

            numberOfCompares = 0;

            if (size == priorityQueue.length - 1) {
                resize(priorityQueue.length * 2);
            }

            size++;
            priorityQueue[size] = key;

            swim(size);

            double lgN = Math.log10(size) / Math.log10(2);
            int lgLgN = lgN != 0 ? (int) (Math.log10(lgN) / Math.log10(2)) : 0;

            StdOut.println("Number of compares: " + numberOfCompares + " Expected: ~" + lgLgN);
        }

        public Key deleteMin() {

            numberOfCompares = 0;

            if (size == 0) {
                throw new RuntimeException("Priority queue underflow");
            }

            size--;

            Key min = priorityQueue[1];

            ArrayUtil.exchange(priorityQueue, 1, size + 1);
            priorityQueue[size + 1] = null;

            sink(1);

            if (size == priorityQueue.length / 4) {
                resize(priorityQueue.length / 2);
            }

            int lgN = (int) (Math.log10(size) / Math.log10(2));
            StdOut.println("Number of compares: " + numberOfCompares + " Expected: ~" + (2 * lgN));

            return min;
        }

        private void swim(int index) {

            //No need to swim if we only have 1 element
            if(index == 1) {
                return;
            }

            int targetAncestor = binarySearchToGetTargetAncestor(index);

            while (index / 2 >= targetAncestor) {
                ArrayUtil.exchange(priorityQueue, index / 2, index);
                index = index / 2;
            }
        }

        private int binarySearchToGetTargetAncestor(int index) {
            //Generate parents array
            int parentsArraySize = (int) (Math.log10(size) / Math.log10(2));
            int[] parentsIndex = new int[parentsArraySize];

            int parentsArrayIndex = parentsIndex.length - 1;
            for(int i = index / 2; i >= 1; i /= 2) {
                parentsIndex[parentsArrayIndex] = i;
                parentsArrayIndex--;
            }

            //Binary search
            int low = 0;
            int high = parentsIndex.length - 1;
            int middle;

            int targetAncestor = index;

            while (low < high) {
                middle = low + (high - low) / 2;

                numberOfCompares++;
                if (ArrayUtil.more(priorityQueue[parentsIndex[middle]], priorityQueue[index])) {
                    high = middle;
                    targetAncestor = parentsIndex[middle];
                } else {
                    low = middle + 1;
                }
            }

            return targetAncestor;
        }

        private void sink(int index) {
            while (index * 2 <= size) {
                int selectedChildIndex = index * 2;

                numberOfCompares++;
                if (index * 2 + 1 <= size && ArrayUtil.more(priorityQueue[index * 2], priorityQueue[index * 2 + 1])) {
                    selectedChildIndex = index * 2 + 1;
                }

                numberOfCompares++;
                if (ArrayUtil.less(priorityQueue[selectedChildIndex], priorityQueue[index])) {
                    ArrayUtil.exchange(priorityQueue, index, selectedChildIndex);
                } else {
                    break;
                }

                index = selectedChildIndex;
            }
        }

        private void resize(int newSize) {
            Key[] newPriorityQueue = (Key[]) new Comparable[newSize];
            System.arraycopy(priorityQueue, 1, newPriorityQueue, 1, size);
            priorityQueue = newPriorityQueue;
        }
    }

    public static void main(String[] args) {
        Exercise31_FastInsert.PriorityQueueFastInsert<Integer> priorityQueueFastInsert =
                new Exercise31_FastInsert().new PriorityQueueFastInsert<>();

        //Insert many items and later insert smaller items to check the number of compares
        for(int i=10; i <= 42; i++) {
            priorityQueueFastInsert.insert(i);
        }

        priorityQueueFastInsert.insert(2);
        priorityQueueFastInsert.insert(1);

        //Also test delete min
        StdOut.println("Delete Min: " + priorityQueueFastInsert.deleteMin() + " Expected: 1");
        StdOut.println("Delete Min: " + priorityQueueFastInsert.deleteMin() + " Expected: 2");
        StdOut.println("Delete Min: " + priorityQueueFastInsert.deleteMin() + " Expected: 10");
    }

}
