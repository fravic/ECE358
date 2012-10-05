class Simulator {
    public static void main(String[] args) {
        /* Initialise important terms such as t_arrival = exponential r.v, # of pkts in queue = 0, 
           t_departure = t_arrival ( this implies that first time departure will be called as soon as a 
           packet arrives in the queue */
    }

    private void startSimulation(int ticks) {
        for (int t = 1; t <= ticks; t++) {
            arrival(t);
            departure(t);
        }
    }

    private void arrival(int t) {
        /* Generate a packet as per the exponential distribution and insert the packet in the 
           queue (an array or a linked list) */
    }

    private void departure(int t) {
        /* Check the queue for the packet, if head of the queue is empty, return 0 else if the 
           queue is non-empty delete the packet from the queue after an elapse of the 
           deterministic service time. */
    }

    private void computePerformances() {
        /* Calculate and display the results such as average number of packets in queue, 
           average delay in queue and idle time for the server. */
    }
}