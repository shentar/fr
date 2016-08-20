package fr;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;

public class AppConfig
{
    public static final String configFilePath = "fr.xml";

    private static XMLConfiguration config;

    private static final AppConfig instance = new AppConfig();

    private AppConfig()
    {

    }

    public static AppConfig getInstance()
    {
        return instance;
    }

    static
    {
        try
        {
            config = new XMLConfiguration(configFilePath);
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            strategy.setRefreshDelay(5000);
            config.setReloadingStrategy(strategy);
        }
        catch (ConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public int getThreadCount()
    {
        return config.getInt("threadcount", 20);
    }

    public boolean isIgnoreCase()
    {
        return config.getBoolean("ignorecase");
    }

    public List<String> getInputDir()
    {
        List<String> dirlst = new LinkedList<String>();
        try
        {
            List<String> strList = new LinkedList<String>();
            List<Object> lst = config.getList("inputdir.dir");
            for (Object s : lst)
            {
                if (s instanceof String)
                {
                    strList.add((String) s);
                }
            }

            for (String s : strList)
            {
                File f = new File(s);
                if (f.exists() && f.isDirectory())
                {
                    dirlst.add(f.getCanonicalPath());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dirlst;
    }

    public List<ReplacePair> getRp()
    {
        List<Object> l = config.getList("replacepair.pair.left");
        List<Object> r = config.getList("replacepair.pair.right");
        if (l.size() != r.size())
        {
            System.out.println("the config is error!");
            return null;
        }

        List<ReplacePair> lr = new ArrayList<ReplacePair>();
        Iterator<Object> itl = l.iterator();
        Iterator<Object> itr = r.iterator();

        for (;;)
        {
            if (itl.hasNext() && itr.hasNext())
            {
                String ls = (String) (itl.next());
                String rs = (String) (itr.next());
                if (StringUtils.isBlank(ls))
                {
                    continue;
                }

                lr.add(new ReplacePair(ls, rs));
            }
            else
            {
                break;
            }
        }

        System.out.println(lr);

        return lr;
    }

    public List<String> getFileSuffix()
    {
        List<String> dirLst = new LinkedList<String>();
        List<Object> lst = config.getList("filesuffix.suffix");
        for (Object s : lst)
        {
            if (s instanceof String)
            {
                dirLst.add("." + ((String) s).toLowerCase());
            }
        }

        return dirLst;
    }

    public boolean isDos()
    {
        return "dos".equalsIgnoreCase(config.getString("unixstyle"));
    }
}
