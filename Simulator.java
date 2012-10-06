/*
  Simulator.java

  Arguments
  - n: Number of ticks to perform, in seconds (NOT MICROSECONDS)
  - lambda: Average number of packets generated/arrived per second
  - L: Length of each packet, in bits
  - C: Transmission rate of the output link in bits per second
  - k: Buffer size limit, or 0 for infinite

  Example usage: java Simulator 5000 5 10 50
*/

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

class Simulator {

    int MICROSECONDS = 1000000;

    // Parameters
    private int _packetsPerSecond;
    private int _packetLength;
    private int _transmissionRate;
    private int _bufferSizeLimit;

    // Utility vars
    private Queue _queue;
    private int _nextArrivalTime;
    private int _serviceStartTime;
    private int _serviceTime;
    
    // Performance metrics
    private long _cumulativePacketsInQueue;
    private long _numPacketsProcessed;
    private long _totalSojurnTime;
    private long _totalIdleTime;
    private long _numPacketsDropped;

    public Simulator(int packetsPerSecond, int packetLength, int transmissionRate, int bufferSizeLimit) {
        _packetsPerSecond = packetsPerSecond;
        _packetLength = packetLength;
        _transmissionRate = transmissionRate;
        _bufferSizeLimit = bufferSizeLimit;

        _queue = new LinkedList<Integer>();
        _nextArrivalTime = 0;
        _serviceStartTime = -1; // Begin not servicing
        _serviceTime = (int)(((double)_packetLength / (double)_transmissionRate) * MICROSECONDS);
        System.out.println("Service time: " + _serviceTime);
    }

    public void startSimulation(int T) {
        _cumulativePacketsInQueue = 0;
        _numPacketsProcessed = 0;
        _totalIdleTime = 0;
        _totalSojurnTime = 0;
        _numPacketsDropped = 0;

        for (int t = 0; t < T * MICROSECONDS; t++) {
            arrival(t);
            departure(t);
        }
        computePerformance(T * MICROSECONDS);
    }

    /* Generate a packet as per the exponential distribution and insert the packet in the 
       queue (an array or a linked list) */
    private void arrival(int t) {
        if (shouldGeneratePacket(t)) {
            if (_bufferSizeLimit <= 0 || (_queue.size() < _bufferSizeLimit)) {
                _queue.add(new Integer(t));
            } else {
                _numPacketsDropped++;
            }
        }
    }

    /* Check the queue for the packet, if head of the queue is empty, return 0 else if the 
       queue is non-empty delete the packet from the queue after an elapse of the 
       deterministic service time. */
    private void departure(int t) {
        _cumulativePacketsInQueue += _queue.size();

        // If the queue is empty, we have some idle time
        if (_queue.peek() == null) {
            _totalIdleTime++;
            return;
        }

        // Otherwise, if we're not servicing yet, start servicing
        if (_serviceStartTime < 0) {
            _serviceStartTime = t;
        } else if (t >= _serviceStartTime + _serviceTime) {
            // Done servicing, remove from queue
            int packetStartTime = ((Integer)_queue.remove()).intValue();
            _totalSojurnTime += t - packetStartTime; 
            _numPacketsProcessed++;

            // Stop servicing
            _serviceStartTime = -1;
        }
    }

    private boolean shouldGeneratePacket(int t) {
        if (t >= _nextArrivalTime) {
            _nextArrivalTime += (int)(generateArrivalTime() * MICROSECONDS);
            return true;
        }
        return false;
    }

    private double generateArrivalTime() {
        Random generator = new Random();
        double uniform = generator.nextDouble();
        double arrivalTime = Math.log(1 - uniform) / (-_packetsPerSecond);
        return arrivalTime;
    }

    private void computePerformance(int ticks) {
        // Average number of packets in queue
        System.out.println("Avergae number of packets in queue: " + (_cumulativePacketsInQueue / (double)ticks));
        
        // Average time spent in queue per packet
        System.out.println("Average sojurn time: " + (_totalSojurnTime / (double)_numPacketsProcessed));

        // Percentage idle time
        System.out.println("Percentage idle time: " + ((_totalIdleTime / (double)ticks) * 100) + "%");

        // Packet loss probability
        System.out.println("Packet loss proability: " + ((_numPacketsDropped / (double)(_numPacketsDropped + _numPacketsProcessed)) * 100) + "%");
    }

    public static void main(String[] argv) {
        if (argv.length < 4) {
            System.out.println("Usage: java Simulator T lambda L C");
            System.exit(0);
        }

        int bufferSizeLimit = 0;
        if (argv.length >= 5) {
            bufferSizeLimit = Integer.parseInt(argv[4]);
        }

        Simulator s = new Simulator(
                                    Integer.parseInt(argv[1]),
                                    Integer.parseInt(argv[2]),
                                    Integer.parseInt(argv[3]),
                                    bufferSizeLimit
                                    );
        s.startSimulation(Integer.parseInt(argv[0]));
        
        System.exit(0);
    }
}