package dk.xenos.computer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

public class ContractSelectorTest {
    @Test public void stableDailyAndWeeklyRotations() {
        UUID player=UUID.fromString("00000000-0000-0000-0000-000000000001");
        List<String> pool=Arrays.asList("stone","coal","iron","sand","gravel","fish");
        List<String> a=ContractSelector.pick(player,"2026-10-25",false,pool,3);
        assertEquals(3,a.size());
        assertEquals(3,new HashSet<String>(a).size());
        assertEquals(a,ContractSelector.pick(player,"2026-10-25",false,pool,3));
        assertEquals(pool.size(),new HashSet<String>(pool).size());
    }
    @Test public void handlesSmallPools() {
        List<String> picks=ContractSelector.pick(UUID.randomUUID(),"day",true,Arrays.asList("stone"),3);
        assertEquals(Arrays.asList("stone"),picks);
    }
    @Test(expected=IllegalArgumentException.class)
    public void rejectsInvalidSize() {
        ContractSelector.pick(UUID.randomUUID(),"day",false,Arrays.asList("stone"),-1);
    }
}
