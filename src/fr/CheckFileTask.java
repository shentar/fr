package fr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CheckFileTask implements Runnable
{
    private File f;

    public CheckFileTask(File fin)
    {
        this.f = fin;
    }

    public void run()
    {
        checkOneFile(f);
        ToolMain.changeCount(false);
    }

    private void checkOneFile(File f)
    {
        BufferedReader bf = null;
        BufferedWriter of = null;
        try
        {
            bf = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));

            Map<Integer, String> lines = new TreeMap<Integer, String>();
            int i = 0;
            while (true)
            {
                String line = bf.readLine();
                i++;
                if (line == null)
                {
                    break;
                }

                lines.put(i, line);
            }

            Map<Integer, String> changed = new TreeMap<Integer, String>();
            for (Entry<Integer, String> l : lines.entrySet())
            {
                for (ReplacePair rp : ToolMain.getLr())
                {
                    if (!ToolMain.isIgnoreCase())
                    {
                        if (l.getValue().contains(rp.getLeft()))
                        {
                            String newLine = l.getValue().replace(rp.getLeft(), rp.getRight());
                            changed.put(l.getKey(), newLine);
                        }
                    }
                }
            }

            if (changed.isEmpty())
            {
                return;
            }

            synchronized (System.out)
            {
                System.out.println("File: " + f.getCanonicalPath());
                for (Entry<Integer, String> cl : changed.entrySet())
                {
                    System.out
                            .print("Line: " + cl.getKey() + " Content: " + lines.get(cl.getKey()));
                    lines.put(cl.getKey(), cl.getValue());
                    System.out.println(" New Content: " + cl.getValue());

                }
            }

            File tmp = new File(f.getCanonicalPath() + "tmpnow");
            if (tmp.exists())
            {
                tmp = new File(f.getCanonicalPath() + "tmpnow" + "_tmpnow");
                if (tmp.exists())
                {
                    return;
                }
            }

            of = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8));

            String rn = ToolMain.isDos() ? "\r\n" : "\n";
            for (Entry<Integer, String> l : lines.entrySet())
            {
                of.write(l.getValue() + rn);
            }

            of.close();
            of = null;
            bf.close();
            bf = null;

            f.delete();
            tmp.renameTo(f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bf != null)
            {
                try
                {
                    bf.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (of != null)
            {
                try
                {
                    of.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
