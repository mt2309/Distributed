/**
 * User: mthorpe
 * Date: 13/03/2013
 * Time: 21:58
 */
public class StrongFailureDetector extends EventuallyPerfectFailureDetector {

    int leader;

    public StrongFailureDetector(Process p) {
        super(p);
        // leader starts off with the highest pid'ed process
        leader = p.getNo();
    }

    @Override
    public boolean isSuspect(Integer process) {
        return super.isSuspect(process);
    }

}
