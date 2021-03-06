package fr;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ToolMain
{
    private static AtomicLong filecount = new AtomicLong(0);

    private static final ExecutorService threadPool = Executors
            .newFixedThreadPool(AppConfig.getInstance().getThreadCount());

    private static boolean isIgnoreCase = AppConfig.getInstance().isIgnoreCase();

    private static List<String> filesufixs = AppConfig.getInstance().getFileSuffix();

    private static List<ReplacePair> lr = AppConfig.getInstance().getRp();

    private static List<String> indirs = AppConfig.getInstance().getInputDir();

    private static boolean dos = AppConfig.getInstance().isDos();

    public static void main(String args[])
    {
        if (lr == null || lr.isEmpty() || indirs.isEmpty())
        {
            System.out.println("error configuration!");
            return;
        }
        System.out.println("folders: " + indirs);
        System.out.println("file types: " + filesufixs);
        System.out.println("new file style: " + (dos ? "dos" : "unix"));
        System.err.print("即将按照如上规则和指定目录替换所有文件中的符合要求的内容，确认继续吗？(y or n): ");

        try
        {
            char c = (char) System.in.read();
            if (c != 'y' && c != 'Y')
            {
                System.out.println("Canceled!");
                return;
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            return;
        }

        for (String fpath : indirs)
        {
            changeCount(true);
            threadPool.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        mapAllfiles(new File(fpath));

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        changeCount(false);
                    }

                }
            });
        }

        while (filecount.get() != 0)
        {
            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        threadPool.shutdown();
    }

    public static void changeCount(boolean isAdd)
    {
        if (isAdd)
        {
            filecount.incrementAndGet();
        }
        else
        {
            filecount.decrementAndGet();
        }
    }

    public static void mapAllfiles(final File f) throws IOException
    {
        if (f == null)
        {
            return;
        }

        if (f.isFile())
        {
            String filename = f.getCanonicalPath();

            boolean carefile = false;
            if (filesufixs == null || filesufixs.isEmpty())
            {
                carefile = true;
            }

            if (filesufixs != null)
            {
                for (String s : filesufixs)
                {
                    if (filename.toLowerCase().endsWith(s))
                    {
                        carefile = true;
                        break;
                    }
                }
            }

            if (!carefile)
            {
                return;
            }

            changeCount(true);
            threadPool.submit(new CheckFileTask(f));

            return;
        }

        File[] files = f.listFiles();
        if (files == null)
        {
            return;
        }

        for (File cf : files)
        {
            mapAllfiles(cf);
        }
    }

    public static boolean isIgnoreCase()
    {
        return isIgnoreCase;
    }

    public static void setIgnoreCase(boolean isIgnoreCase)
    {
        ToolMain.isIgnoreCase = isIgnoreCase;
    }

    public static List<String> getFilesufixs()
    {
        return filesufixs;
    }

    public static void setFilesufixs(List<String> filesufixs)
    {
        ToolMain.filesufixs = filesufixs;
    }

    public static List<ReplacePair> getLr()
    {
        return lr;
    }

    public static void setLr(List<ReplacePair> lr)
    {
        ToolMain.lr = lr;
    }

    public static List<String> getIndirs()
    {
        return indirs;
    }

    public static void setIndirs(List<String> indirs)
    {
        ToolMain.indirs = indirs;
    }

    public static boolean isDos()
    {
        return dos;
    }

    public static void setDos(boolean d)
    {
        ToolMain.dos = d;
    }

}
