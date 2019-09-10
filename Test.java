import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Stack;

public class Test
{
    public static void main(String[] args)
    {
        testDiskConstr();
        testDiskCompare();
        testDiskString();
        testRodConstr();
        testRodEmpty();
        testRodFull();
        testRodPush();
        testRodPop();
        testRodPeek();
        testRodCompare();
        testRodString();
        testTowerConstr();
        testTowerMove();
        testTowerIsSolved();
        testTowerString();
        testTowerSolve();
    }
    
    private static void testTowerSolve()
    {
        System.out.println("Testing TowerOfHanoi solve...");
        // hijack system.out to test for printouts
        PrintStream out = System.out;
        PrintStealer stealer = new PrintStealer(out);
        System.setOut(stealer);
        stealer.allow = false;
        
        int n = 7;
        for (int i = 0; i <= n; i++)
        {
            stealer.reset();
            TowerOfHanoi t = new TowerOfHanoi(3, i);
            t.solve(i, 0, 2, 1);
            if (!t.isSolved())
            {
                System.setOut(out);
                System.out.println("Failed: t.solve failed to solve the puzzle 3, " + i);
                return;
            }
            if (stealer.printouts.size() != (int) Math.pow(2, i))
            {
                System.setOut(out);
                System.out.println("Failed: t.solve failed to solve the puzzle 3, " + i + " in "
                        + (int) Math.pow(2, i) + " turns. " + stealer.printouts.size()
                        + " turns were used.");
                return;
            }
        }
        
        System.setOut(out);
        System.out.println("Passed.");
    }
    
    private static void testTowerString()
    {
        System.out.println("Testing TowerOfHanoi toString...");
        int n = 5;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n - i; j++)
            {
                for (int k = 0; k < n - (i + j); k++)
                {
                    // generate the tower
                    TowerOfHanoi t = new TowerOfHanoi(3, n);
                    Rod[] rods = getTowerRods(t);
                    int d = n;
                    rods[0] = new Rod(n, 0);
                    rods[1] = new Rod(n, 0);
                    rods[2] = new Rod(n, 0);
                    for (int a = 0; a < i; a++)
                    {
                        rods[0].push(new Disk(d));
                        d--;
                    }
                    for (int a = 0; a < j; a++)
                    {
                        rods[1].push(new Disk(d));
                        d--;
                    }
                    for (int a = 0; a < k; a++)
                    {
                        rods[2].push(new Disk(d));
                        d--;
                    }
                    // generate the regex
                    String re = "";
                    for (int r = n - 1; r >= 0; r--)
                    {
                        for (int c = 0; c < 3; c++)
                        {
                            Rod rod = rods[c];
                            Disk[] disks = getRodDisks(rod);
                            if (r < getRodNum(rod))
                            {
                                int spaces = ((disks.length * 2 + 1) - disks[r].toString().length())
                                        / 2;
                                int dsize = getDiskSize(disks[r]);
                                re = re + "[ ]{" + spaces + "}[<]{1}[=]{" + (dsize - 1) + "}["
                                        + dsize + "]{1}[=]{" + (dsize - 1) + "}[>]{1}[ ]{" + spaces
                                        + "}";
                            }
                            else
                            {
                                int spaces = disks.length;
                                re = re + "[ ]{" + spaces + "}[\\|]{1}[ ]{" + spaces + "}";
                            }
                        }
                        re = re + "[\\n]{1}";
                    }
                    // check regex
                    String tres = t.toString();
                    if (!tres.matches(re))
                    {
                        if (tres.charAt(tres.length() - 1) != '\n')
                        {
                            System.out.println(
                                    "Failed: TowerOfHanoi.toString() must end with a \\n character");
                            return;
                        }
                        System.out.println("Failed: TowerOfHanoi.toString() gave the wrong output");
                        return;
                    }
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testTowerIsSolved()
    {
        System.out.println("Testing TowerOfHanoi isSolved...");
        int n = 9;
        TowerOfHanoi t = new TowerOfHanoi(n, n);
        Rod[] rods = getTowerRods(t);
        int k = 4;
        for (int i = 0; i < n; i++)
        {
            if (t.isSolved())
            {
                System.out.println("Failed: Reported an unfinished game state as solved.");
                return;
            }
            rods[k].push(rods[0].pop());
        }
        for (int i = 0; i < n; i++)
        {
            if (t.isSolved())
            {
                System.out.println("Failed: Reported an unfinished game state as solved.");
                return;
            }
            rods[n - 1].push(rods[k].pop());
        }
        if (!t.isSolved())
        {
            System.out.println("Failed: Reported a finished game state as unsolved.");
            return;
        }
        System.out.println("Passed.");
    }
    
    private static Stack<Disk> rodToStack(Rod r)
    {
        Stack<Disk> st = new Stack<Disk>();
        Disk[] disks = getRodDisks(r);
        for (int k = 0; k < getRodNum(r); k++)
        {
            st.push(disks[k]);
        }
        return st;
    }
    
    private static void testTowerMove()
    {
        System.out.println("Testing TowerOfHanoi moveDisk...");
        // hijack system.out to test for printouts
        PrintStream out = System.out;
        PrintStealer stealer = new PrintStealer(out);
        System.setOut(stealer);
        stealer.allow = false;
        
        int n = 9;
        TowerOfHanoi t = new TowerOfHanoi(n, n);
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                boolean fail = true;
                Rod[] r = getTowerRods(t);
                int c = r[i].compareTo(r[j]);
                if (c == 1)
                {
                    fail = false;
                }
                // copy stacks
                Stack<Disk> si = rodToStack(r[i]);
                Stack<Disk> sj = rodToStack(r[j]);
                // test move
                stealer.reset();
                t.moveDisk(i, j);
                // if fail...
                if (fail)
                {
                    // check for lack of error message
                    if (!stealer.getLast().equals("WARNING: Illegal move.\n"))
                    {
                        System.setOut(out);
                        System.out.println(
                                "Failed: Did not print out \"WARNING: Illegal move.\" where compareTo was "
                                        + c);
                        return;
                    }
                    // nothing should have changed
                    if (!(testEquality(r[i], si) && testEquality(r[j], sj)))
                    {
                        System.setOut(out);
                        System.out.println("Failed: Modified rods even though compareTo was " + c);
                        return;
                    }
                }
                else
                {
                    // check for error message
                    if (!stealer.getLast().equals(""))
                    {
                        System.setOut(out);
                        System.out.println("Failed: Printed out \"" + stealer.getLast()
                                + "\" where nothing should have been printed, since compareTo was "
                                + c);
                        return;
                    }
                    // check against desired solution
                    sj.push(si.pop());
                    if (!(testEquality(r[i], si) && testEquality(r[j], sj)))
                    {
                        System.setOut(out);
                        System.out.println("Failed: Disk move did not match expected results.");
                        return;
                    }
                }
            }
        }
        
        // restore old system.out
        System.setOut(out);
        System.out.println("Passed.");
    }
    
    private static void testTowerConstr()
    {
        System.out.println("Testing TowerOfHanoi constructor...");
        
        for (int i = 1; i <= 9; i++)
        {
            for (int j = 0; j <= 9; j++)
            {
                TowerOfHanoi t = null;
                try
                {
                    t = new TowerOfHanoi(i, j);
                }
                catch (Exception e)
                {
                    System.out.println(
                            "Failed: The TowerOfHanoi constructor threw an exception when given width = "
                                    + i + " and height = " + j + "; this is not supposed happen.");
                    e.printStackTrace(System.out);
                    return;
                }
                if (t != null)
                {
                    Rod[] rods = getTowerRods(t);
                    if (rods == null)
                    {
                        System.out.println(
                                "Failed: TowerOfHanoi.rods was found to be null or not declared.");
                        return;
                    }
                    if (rods.length != i)
                    {
                        System.out.println("Failed: The length of the rods array is " + rods.length
                                + ", but the width passed in was + " + i
                                + "; The constructor was given width = " + i + " and height = "
                                + j);
                        return;
                    }
                    for (int k = 0; k < i; k++)
                    {
                        Rod r = rods[k];
                        if (r == null)
                        {
                            
                        }
                        Disk[] ds = getRodDisks(r);
                        if (ds.length != j)
                        {
                            System.out.println("Failed: The height of the rod at position " + k
                                    + " is " + ds.length + ", but the height passed in was + " + j
                                    + "; The constructor was given width = " + i + " and height = "
                                    + j);
                            return;
                        }
                        if (k == 0 && !r.isFull())
                        {
                            System.out.println(
                                    "Failed: The first rod is not full, and its contents are "
                                            + Arrays.toString(ds)
                                            + "; The constructor was given width = " + i
                                            + " and height = " + j);
                            return;
                        }
                        if (k > 0 && !r.isEmpty())
                        {
                            System.out.println("Failed: The rod after the first rod, at position "
                                    + k + ", is not empty, and its contents are "
                                    + Arrays.toString(ds) + "; The constructor was given width = "
                                    + i + " and height = " + j);
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static Rod[] getTowerRods(TowerOfHanoi t)
    {
        Rod[] rods = null;
        try
        {
            Field f = t.getClass().getDeclaredField("rods"); // NoSuchFieldException
            f.setAccessible(true);
            rods = (Rod[]) f.get(t); // IllegalAccessException
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed: TowerOfHanoi class does not have a field named 'rods'");
        }
        return rods;
    }
    
    private static void testRodString()
    {
        System.out.println("Testing Rod toString...");
        for (int i = 1; i <= 9; i++)
        {
            for (int j = 0; j < i; j++)
            {
                Rod r = new Rod(i, j);
                Disk[] disks = getRodDisks(r);
                String re = "";
                for (int k = 0; k < disks.length; k++)
                {
                    String s = "|";
                    if (k < j)
                    {
                        s = disks[k].toString();
                        int spaces = ((disks.length * 2 + 1) - s.length()) / 2;
                        int d = getDiskSize(disks[k]);
                        re = "[ ]{" + spaces + "}[<]{1}[=]{" + (d - 1) + "}[" + d + "]{1}[=]{"
                                + (d - 1) + "}[>]{1}[ ]{" + spaces + "}" + "[\\n]{1}" + re;
                    }
                    else
                    {
                        int spaces = disks.length;
                        re = "[ ]{" + spaces + "}[\\|]{1}[ ]{" + spaces + "}" + "[\\n]{1}" + re;
                    }
                }
                String rres = r.toString();
                if (!rres.matches(re))
                {
                    if (rres.charAt(rres.length() - 1) != '\n')
                    {
                        System.out.println("Failed: Rod.toString() must end with a \\n character");
                        return;
                    }
                    System.out.println("Failed: Rod.toString() on Rod with maxHeight = " + i
                            + " and numberOfDisks = " + j + " gave the wrong output:");
                    System.out.println(rres);
                    return;
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testRodCompare()
    {
        System.out.println("Testing Rod compareTo...");
        Rod r = new Rod(3, 1);
        try
        {
            int n = r.compareTo(null);
            if (n != 0)
            {
                System.out.println("Failed: compareTo(null) returned " + n + ", not 0");
                return;
            }
            r = new Rod(3, 0);
            n = r.compareTo(new Rod(3, 1));
            if (n != 0)
            {
                System.out.println("Failed: compareTo(other) when empty returned " + n + ", not 0");
                return;
            }
            r = new Rod(3, 1);
            n = r.compareTo(new Rod(3, 0));
            if (n != 1)
            {
                System.out.println(
                        "Failed: compareTo(empty) when not empty returned " + n + ", not 1");
                return;
            }
            r = new Rod(3, 2);
            r.pop();
            n = r.compareTo(new Rod(3, 1));
            if (n != -1)
            {
                System.out.println("Failed: compareTo(larger top disk) returned " + n + ", not -1");
                return;
            }
            r = new Rod(3, 1);
            Rod s = new Rod(3, 2);
            s.pop();
            n = r.compareTo(s);
            if (n != 1)
            {
                System.out.println("Failed: compareTo(smaller top disk) returned " + n + ", not 1");
                return;
            }
            r = new Rod(3, 1);
            s = new Rod(3, 0);
            s.push(new Disk(4));
            s.push(new Disk(3));
            s.push(new Disk(2));
            n = r.compareTo(s);
            if (n != -1)
            {
                System.out.println("Failed: compareTo(full) returned " + n + ", not -1");
                return;
            }
        }
        catch (NullPointerException e)
        {
            System.out
                    .println("Failed: NullPointerException was thrown. Are you checking for null?");
            return;
        }
        System.out.println("Passed.");
    }
    
    private static void testRodPeek()
    {
        System.out.println("Testing Rod peek...");
        for (int i = 0; i < 10; i++)
        {
            Rod r = new Rod(i, 0);
            boolean caught = false;
            try
            {
                r.peek();
            }
            catch (NoSuchElementException e)
            {
                caught = true;
            }
            catch (Exception e)
            {
                System.out.println(
                        "Failed: threw the wrong exception type (should be NoSuchElementException) when peeking on an empty rod.");
                return;
            }
            if (!caught)
            {
                System.out.println(
                        "Failed: did not throw NoSuchElementException when peeking on an empty rod.");
                return;
            }
        }
        for (int i = 1; i < 10; i++)
        {
            Rod r = new Rod(i, i);
            Stack<Disk> s = rodToStack(r);
            for (int j = 0; j < i; j++)
            {
                Disk rpeek;
                Disk speek;
                try
                {
                    rpeek = r.peek();
                    speek = s.peek();
                }
                catch (Exception e)
                {
                    System.out.println("Failed: Peek threw an exception when it had elements: ");
                    System.out.println(stackToRodList(s, i));
                    e.printStackTrace(System.out);
                    return;
                }
                if (rpeek != speek)
                {
                    System.out.println("Failed: Peek returned the wrong element, expected " + speek
                            + ", got " + rpeek);
                    return;
                }
                // check equality
                if (!testEquality(r, s))
                {
                    return;
                }
                // check for s with nulls
                if (s.contains(null))
                {
                    System.out.println(
                            "Failed: Rod contains null elements in positions under numberOfDisks: ");
                    System.out.println(stackToRodList(s, i));
                    return;
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testRodPop()
    {
        System.out.println("Testing Rod pop...");
        for (int i = 0; i < 10; i++)
        {
            Rod r = new Rod(i, 0);
            boolean caught = false;
            try
            {
                r.pop();
            }
            catch (NoSuchElementException e)
            {
                caught = true;
            }
            catch (Exception e)
            {
                System.out.println(
                        "Failed: threw the wrong exception type (should be NoSuchElementException) when popping from an empty rod.");
                return;
            }
            if (!caught)
            {
                System.out.println(
                        "Failed: did not throw NoSuchElementException when popping from an empty rod.");
                return;
            }
        }
        for (int i = 1; i < 10; i++)
        {
            Rod r = new Rod(i, i);
            Stack<Disk> s = rodToStack(r);
            for (int j = 0; j < i; j++)
            {
                try
                {
                    Disk rpop = r.pop();
                    Disk spop = s.pop();
                    if (rpop != spop)
                    {
                        System.out.println("Failed: Pop returned the wrong element, expected "
                                + spop + ", got " + rpop);
                        return;
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Failed: Pop threw an exception when it had elements: ");
                    System.out.println(stackToRodList(s, i));
                    e.printStackTrace(System.out);
                    return;
                }
                // check equality
                if (!testEquality(r, s))
                {
                    return;
                }
                // check for s with nulls
                if (s.contains(null))
                {
                    System.out.println(
                            "Failed: Rod contains null elements in positions under numberOfDisks: ");
                    System.out.println(stackToRodList(s, i));
                    return;
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testRodPush()
    {
        System.out.println("Testing Rod push...");
        for (int i = 0; i < 10; i++)
        {
            Rod r = new Rod(i, i);
            boolean caught = false;
            try
            {
                r.push(new Disk(2));
            }
            catch (IllegalStateException e)
            {
                caught = true;
            }
            catch (Exception e)
            {
                System.out.println(
                        "Failed: threw the wrong exception type (should be IllegalStateException) when pushing onto a full rod.");
                return;
            }
            if (!caught)
            {
                System.out.println(
                        "Failed: did not throw IllegalStateException when pushing onto a full rod.");
                return;
            }
        }
        for (int i = 1; i < 10; i++)
        {
            Stack<Disk> s = new Stack<Disk>();
            Rod r = new Rod(i, 0);
            for (int j = 0; i < i; j++)
            {
                Disk d = new Disk(j);
                try
                {
                    r.push(d);
                    s.push(d);
                }
                catch (Exception e)
                {
                    System.out.println("Failed: Push threw an exception when it had elements: ");
                    System.out.println(stackToRodList(s, i));
                    e.printStackTrace(System.out);
                    return;
                }
                // check equality
                if (!testEquality(r, s))
                {
                    return;
                }
                // check for s with nulls
                if (s.contains(null))
                {
                    System.out.println(
                            "Failed: Rod contains null elements in positions under numberOfDisks: ");
                    System.out.println(stackToRodList(s, i));
                    return;
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testRodEmpty()
    {
        System.out.println("Testing Rod empty...");
        Stack<Disk> st = new Stack<Disk>();
        int s = 7;
        Rod r = new Rod(s, 0);
        if (!r.isEmpty())
        {
            System.out.println("Failed: Rod should be empty, but isEmpty returned false");
            return;
        }
        try
        {
            for (int i = 1; i < s; i++)
            {
                Disk d = new Disk(i);
                r.push(d);
                st.push(d);
                if (r.isEmpty())
                {
                    System.out.println(
                            "Failed: Rod should not be empty, but isEmpty returned true on expected contents "
                                    + stackToRodList(st, s).toString());
                    return;
                }
            }
            for (int i = 1; i < s; i++)
            {
                Disk d = new Disk(7);
                r.push(d);
                st.push(d);
                r.pop();
                st.pop();
                if (r.isEmpty())
                {
                    System.out.println(
                            "Failed: Rod should not be empty, but isEmpty returned true on expected contents "
                                    + stackToRodList(st, s).toString());
                    return;
                }
            }
            for (int i = 1; i < s; i++)
            {
                if (r.isEmpty())
                {
                    System.out.println(
                            "Failed: Rod should not be empty, but isEmpty returned true on expected contents "
                                    + stackToRodList(st, s).toString());
                    return;
                }
                r.pop();
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed: Rod threw an exception:");
            e.printStackTrace(System.out);
            return;
        }
        if (!r.isEmpty())
        {
            System.out.println("Failed: Rod should be empty, but isEmpty returned false");
            return;
        }
        System.out.println("Passed.");
    }
    
    private static void testRodFull()
    {
        System.out.println("Testing Rod full...");
        Stack<Disk> st = new Stack<Disk>();
        int s = 7;
        Rod r = new Rod(s, 0);
        if (r.isFull())
        {
            System.out.println("Failed: Rod should be empty, but isFull returned true");
            return;
        }
        try
        {
            for (int i = 1; i < s; i++)
            {
                if (r.isFull())
                {
                    System.out.println(
                            "Failed: Rod should not be full, but isFull returned on expected contents "
                                    + stackToRodList(st, s).toString());
                    return;
                }
                Disk d = new Disk(i);
                r.push(d);
                st.push(d);
            }
            for (int i = 1; i < s; i++)
            {
                Disk d = new Disk(7);
                r.push(d);
                st.push(d);
                if (!r.isFull())
                {
                    System.out.println("Failed: Rod should be full, but isFull returned false");
                    return;
                }
                r.pop();
                st.pop();
            }
            for (int i = 1; i < s; i++)
            {
                r.pop();
                if (r.isFull())
                {
                    System.out.println(
                            "Failed: Rod should not be full, but isFull returned true on expected contents "
                                    + stackToRodList(st, s).toString());
                    return;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed: Rod threw an exception.");
            e.printStackTrace(System.out);
            return;
        }
        if (r.isFull())
        {
            System.out.println("Failed: Rod should be empty, but isFull returned true");
            return;
        }
        System.out.println("Passed.");
    }
    
    private static boolean testEquality(Rod r, Stack<Disk> s)
    {
        // size
        if (s.size() != getRodNum(r))
        {
            System.out.println(
                    "Failed: Expected size " + s.size() + ", but rod size was " + getRodNum(r));
            return false;
        }
        // comparison
        ArrayList<Disk> rt = new ArrayList<Disk>(Arrays.asList(getRodDisks(r)));
        ArrayList<Disk> st = stackToRodList(s, rt.size());
        // elementwise
        boolean result = true;
        for (int i = 0; i < getRodNum(r); i++)
        {
            if (rt.get(i) != st.get(i))
            {
                result = false;
            }
        }
        if (!result)
        {
            System.out.println("Failed: Expected contents " + st.toString()
                    + ", but rod contents were " + rt.toString());
        }
        return true;
    }
    
    private static ArrayList<Disk> stackToRodList(Stack<Disk> s, int size)
    {
        ArrayList<Disk> st = new ArrayList<Disk>(s);
        while (st.size() < size)
        {
            st.add(null);
        }
        return st;
    }
    
    private static void testRodConstr()
    {
        System.out.println("Testing Rod constructor...");
        for (int i = 1; i < 10; i++)
        {
            for (int j = 0; j < i; j++)
            {
                Rod r = null;
                try
                {
                    r = new Rod(i, j);
                }
                catch (Exception e)
                {
                    System.out.println(
                            "Failed: Rod constructor threw an exception on input maxHeight = " + i
                                    + " and numberOfDisks = " + j);
                    return;
                }
                if (r != null)
                {
                    try
                    {
                        int num = getRodNum(r);
                        Disk[] disks = getRodDisks(r);
                        if (disks.length != i)
                        {
                            System.out.println(
                                    "Failed: Rod given maxHeight = " + i + " and numberOfDisks = "
                                            + j + " has maximum height " + disks.length + "");
                            return;
                        }
                        if (num != j)
                        {
                            System.out.println("Failed: Rod given maxHeight = " + i
                                    + " and numberOfDisks = " + j + " has " + num + " disks");
                            return;
                        }
                        for (int k = 0; k < i; k++)
                        {
                            if (k >= j && disks[k] != null)
                            {
                                System.out.println("Failed: Rod given maxHeight = " + i
                                        + " and numberOfDisks = " + j
                                        + " has non-null element at position " + k);
                                return;
                            }
                            if (k < j && disks[k] == null)
                            {
                                System.out.println("Failed: Rod given maxHeight = " + i
                                        + " and numberOfDisks = " + j
                                        + " has null element at position " + k);
                                return;
                            }
                            if (k < j)
                            {
                                int size = getDiskSize(disks[k]);
                                if (size != j - k)
                                {
                                    System.out.println("Failed: Rod given maxHeight = " + i
                                            + " and numberOfDisks = " + j
                                            + " has an element of size " + size + " at position "
                                            + k + " where it should be of size " + (j - k));
                                    return;
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Failed: The Rod constructor threw an exception:");
                        e.printStackTrace(System.out);
                        return;
                    }
                }
            }
        }
        System.out.println("Passed.");
    }
    
    private static Integer getRodNum(Rod r)
    {
        Integer num = Integer.MIN_VALUE;
        try
        {
            Field f = r.getClass().getDeclaredField("numberOfDisks"); // NoSuchFieldException
            f.setAccessible(true);
            num = (Integer) f.get(r); // IllegalAccessException
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed: Rod class does not have a field named 'numberOfDisks'");
        }
        return num;
    }
    
    private static Disk[] getRodDisks(Rod r)
    {
        Disk[] disks = null;
        try
        {
            Field f = r.getClass().getDeclaredField("disks"); // NoSuchFieldException
            f.setAccessible(true);
            disks = (Disk[]) f.get(r); // IllegalAccessException
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed: Rod class does not have a field named 'disks'");
        }
        return disks;
    }
    
    private static Integer getDiskSize(Disk d)
    {
        Integer size = 0;
        try
        {
            Field f = d.getClass().getDeclaredField("size"); // NoSuchFieldException
            f.setAccessible(true);
            size = (int) f.get(d); // IllegalAccessException
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed: Disk class does not have a field named 'size'");
        }
        return size;
    }
    
    private static void testDiskString()
    {
        System.out.println("Testing Disk toString...");
        for (int i = 1; i < 9; i++)
        {
            String re = "[<]{1}[=]{" + (i - 1) + "}[" + i + "]{1}[=]{" + (i - 1) + "}[>]{1}";
            String s = new Disk(i).toString();
            if (!s.matches(re))
            {
                System.out.println(
                        "Failed: Disk.toString() on size " + i + " gave the wrong output: " + s);
                return;
            }
        }
        System.out.println("Passed.");
    }
    
    private static void testDiskCompare()
    {
        System.out.println("Testing Disk compareTo...");
        Disk d = new Disk(3);
        try
        {
            int n = d.compareTo(null);
            if (n != 0)
            {
                System.out.println("Failed: compareTo(null) returned " + n + ", not 0");
            }
            n = d.compareTo(new Disk(3));
            if (n != 0)
            {
                System.out.println("Failed: compareTo(equal disk) returned " + n + ", not 0");
            }
            n = d.compareTo(new Disk(4));
            if (n != -1)
            {
                System.out.println("Failed: compareTo(larger disk) returned " + n + ", not -1");
            }
            n = d.compareTo(new Disk(2));
            if (n != 1)
            {
                System.out.println("Failed: compareTo(smaller disk) returned " + n + ", not 1");
            }
        }
        catch (NullPointerException e)
        {
            System.out
                    .println("Failed: NullPointerException was thrown. Are you checking for null?");
            return;
        }
        System.out.println("Passed.");
    }
    
    private static void testDiskConstr()
    {
        // test Disk constructor normal
        System.out.println("Testing Disk constructor...");
        for (int i = -1; i <= 11; i++)
        {
            boolean shouldFail = false;
            if (i < 1 || i > 9)
            {
                shouldFail = true;
            }
            boolean failed = false;
            Disk d = null;
            try
            {
                d = new Disk(i);
            }
            catch (IllegalArgumentException e)
            {
                failed = true;
            }
            if (shouldFail != failed)
            {
                if (shouldFail)
                {
                    System.out.println(
                            "Failed: Did not throw IllegalArgumentException when size = " + i + "");
                    return;
                }
                else
                {
                    System.out.println(
                            "Failed: Threw IllegalArgumentException when size = " + i + "");
                    return;
                }
            }
            // check if field is set
            if (d != null)
            {
                Integer size = null;
                try
                {
                    Field f = d.getClass().getDeclaredField("size"); // NoSuchFieldException
                    f.setAccessible(true);
                    size = (int) f.get(d); // IllegalAccessException
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Failed: Disk class does not have a field named 'size'");
                    return;
                }
                if (size != i)
                {
                    System.out.println("Failed: The field 'size' is equal to " + size
                            + ", not the expected value of " + i);
                    return;
                }
            }
        }
        System.out.println("Passed.");
    }
}

class PrintStealer extends PrintStream
{
    protected boolean allow = false;
    
    protected ArrayList<String> printouts = new ArrayList<String>();
    
    protected String getLast()
    {
        return printouts.get(printouts.size() - 1);
    }
    
    protected PrintStealer(OutputStream out)
    {
        super(out, true);
    }
    
    protected void reset()
    {
        printouts.clear();
        printouts.add("");
    }
    
    @Override
    public void print(String s)
    {
        if (allow)
        {
            super.print(s);
        }
        printouts.add(s);
    }
    
    @Override
    public void println(Object o)
    {
        print(o.toString() + "\n");
    }
    
    @Override
    public void println(String s)
    {
        print(s + "\n");
    }
}