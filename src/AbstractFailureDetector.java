import java.util.*;

/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 15:32
 */
abstract class AbstractFailureDetector implements IFailureDetector {

    Process p;
    Map<Integer,Integer> processes;
    LinkedList<Integer> suspects;
    LinkedList<Integer> seen;
    Timer t;

    static final int PERIOD = 1000;
    int count = 0;

    class PeriodicTask extends TimerTask {
        public void run() {
            p.broadcast("heartbeat",String.format("%d", System.currentTimeMillis()));

            count += PERIOD;
            for (Integer i : processes.keySet()) {
                if (count > delay(i)) {
                    if (!seen.contains(i)) {
                        suspects.add(i);
                    }
                }
                resetSeenArray();
            }
        }

    }

    public AbstractFailureDetector(Process p) {
        this.p = p;
        this.t = new Timer();
        this.suspects = new LinkedList<>();
        this.processes = new HashMap<>(p.getNo());
        resetSeenArray();
    }

    // Fill the process array with true values
    private void resetSeenArray() {
        seen = new LinkedList<>();
    }

    @Override
    public void begin() {
        t.schedule(new PeriodicTask(),0 , PERIOD);
    }

    @Override
    public void receive(Message m) {
        // given that we passed this message through from the process iff it was a heartbeat
        int source = m.getSource();
        processes.put(source, processes.get(source) + 1);

        // If we suspect this process and we get a heartbeat, its no longer suspect
        if (suspects.contains(source)) {
            suspects.remove(new Integer(source));
        }

        seen.add(source);

        Utils.out(p.pid,m.toString());
    }

    @Override
    public boolean isSuspect(Integer process) {
        return suspects.contains(process);
    }

    @Override
    public int getLeader() {
        return -1;
    }

    @Override
    public void isSuspected(Integer process) {

    }

    abstract int delay(int i);
}
