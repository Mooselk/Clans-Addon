package me.kate.clans.objects;

public class DoubleBound 
{
	private Boundry boundA;
	private Boundry boundB;
	
	public DoubleBound(Boundry a, Boundry b)
	{
		this.boundA = a;
		this.boundB = b;
	}
	
	public Boundry getBoundA()
	{
		return this.boundA;
	}
	
	public Boundry getBoundB()
	{
		return this.boundB;
	}
}
