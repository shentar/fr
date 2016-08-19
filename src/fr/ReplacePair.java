package fr;

public class ReplacePair
{
    private String left;
    private String right;

    public ReplacePair(String l, String r)
    {
        this.left = l;
        this.right = r;
    }

    public String getLeft()
    {
        return left;
    }

    public void setLeft(String left)
    {
        this.left = left;
    }

    public String getRight()
    {
        return right;
    }

    public void setRight(String right)
    {
        this.right = right;
    }

    public String toString()
    {
        return String.format("[%s, %s]", left, right);
    }
}
