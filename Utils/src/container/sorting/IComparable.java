package container.sorting;

import container.SortedArray;

/**
 * An interface to allow it to implement comparisons for any type to sort 
 * {@link SortedArray}s.
 * 
 * @param <T> The type of the instances that will be compared. When used 
 * with a {@link SortedArray}, this type parameter must be the same as the 
 * parameter of the {@link SortedArray}.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see 	EComparisonResult
 * 
 * @param <T>	The type of the elements that will be compared.
 * 
 */
public interface IComparable<T>
{	
	/**
	 * A comparator to sort booleans. First false, then true.
	 */
	public static final IComparable<Boolean> 	DEFAULT_BOOLEAN_COMPARABLE
	= (Boolean source, Boolean other) -> 
	{
		if((source == null && other == null) || source == other)
			return IComparable.EComparisonResult.EQUALS;
		else if(source == null || source == true)
			return IComparable.EComparisonResult.BEFORE;
		else
			return IComparable.EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort bytes in an ascending order. 
	 */
	public static final IComparable<Byte> 		DEFAULT_BYTE_COMPARABLE
	= (Byte source, Byte other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort shorts in an ascending order. 
	 */
	public static final IComparable<Short> 		DEFAULT_SHORT_COMPARABLE
	= (Short source, Short other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort integer in an ascending order. 
	 */
	public static final IComparable<Integer> 	DEFAULT_INT_COMPARABLE
	= (Integer source, Integer other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort longs in an ascending order. 
	 */
	public static final IComparable<Long> 		DEFAULT_LONG_COMPARABLE
	= (Long source, Long other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort floats in an ascending order. 
	 */
	public static final IComparable<Float> 		DEFAULT_FLOAT_COMPARABLE
	= (Float source, Float other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort doubles in an ascending order. 
	 */
	public static final IComparable<Double> 	DEFAULT_DOUBLE_COMPARABLE
	= (Double source, Double other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		else if(source == null || other < source)
			return EComparisonResult.BEFORE;
		else
			return EComparisonResult.AFTER;
	};

	/**
	 * A comparator to sort chars in alphabetical order.
	 */
	public static final IComparable<Character>	DEFAULT_CHAR_COMPERABLE
	= (Character source, Character other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		
		//The ASCII code of the lower case a 
		final int LOWER_A_IN_ASCII = 97;
		
		//Letters between lower case and capital a
		final int LETTERS_BETWEEN_A_IN_ASCII = 32;
		
		Character sourceCapital = (char) (source >= LOWER_A_IN_ASCII ? 
							source - LETTERS_BETWEEN_A_IN_ASCII : source);
		Character otherCapital = (char) (other >= LOWER_A_IN_ASCII ? 
							other - LETTERS_BETWEEN_A_IN_ASCII : other);
		
		if(sourceCapital > otherCapital)
			return IComparable.EComparisonResult.BEFORE;
		else if(sourceCapital < otherCapital)
			return IComparable.EComparisonResult.AFTER;
		else
			return IComparable.EComparisonResult.EQUALS;
	};

	/**
	 * A comparator to sort Strings in alphabetical order.
	 */
	public static final IComparable<String>		
	DEFAULT_ALPHABETICAL_STRING_COMPERABLE = (String source, String other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		
		//Max is the length of the shorter of the two String source and other
		int max = source.length() < other.length() ? 
				source.length() : other.length();
		
		for(int i = 0; i < max; i++)
		{
			switch(DEFAULT_CHAR_COMPERABLE.compare(source.charAt(i), 
					other.charAt(i)))
			{
				case AFTER:
					return IComparable.EComparisonResult.AFTER;
				case BEFORE:
					return IComparable.EComparisonResult.BEFORE;
				default:
					break;
			}
		}
		
		if(source.length() > other.length())
			return IComparable.EComparisonResult.BEFORE;
		else if(source.length() < other.length())
			return IComparable.EComparisonResult.AFTER;
		else
			return IComparable.EComparisonResult.EQUALS;
	};

	/**
	 * A comparator to sort Strings in alphabetical order.
	 */
	public static final IComparable<String>		DEFAULT_SIZE_STRING_COMPERABLE
	= (String source, String other) ->
	{
		if(source == other)
			return EComparisonResult.EQUALS;
		else if(source == null)
			return EComparisonResult.BEFORE;
		else if(other == null)
			return EComparisonResult.AFTER;
		
		if(source.length() > other.length())
			return IComparable.EComparisonResult.BEFORE;
		else if(source.length() < other.length())
			return IComparable.EComparisonResult.AFTER;
		else
			return IComparable.EComparisonResult.EQUALS;
	};

	/**
	 * A comparator to sort booleans. First true, then false.
	 */
	public static final IComparable<Boolean> 	INVERSE_BOOLEAN_COMPARABLE
	= (Boolean source, Boolean other) ->
	{
		return DEFAULT_BOOLEAN_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort bytes in an descending order. 
	 */
	public static final IComparable<Byte> 		INVERSE_BYTE_COMPARABLE
	= (Byte source, Byte other) ->
	{
		return DEFAULT_BYTE_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort short in an descending order. 
	 */
	public static final IComparable<Short> 		INVERSE_SHORT_COMPARABLE
	= (Short source, Short other) ->
	{
		return DEFAULT_SHORT_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort integer in an descending order. 
	 */
	public static final IComparable<Integer> 	INVERSE_INT_COMPARABLE
	= (Integer source, Integer other) ->
	{
		return DEFAULT_INT_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort longs in an descending order. 
	 */
	public static final IComparable<Long> 		INVERSE_LONG_COMPARABLE
	= (Long source, Long other) ->
	{
		return DEFAULT_LONG_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort floats in an descending order. 
	 */
	public static final IComparable<Float> 		INVERSE_FLOAT_COMPARABLE
	= (Float source, Float other) ->
	{
		return DEFAULT_FLOAT_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort doubles in an descending order. 
	 */
	public static final IComparable<Double> 	INVERSE_DOUBLE_COMPARABLE
	= (Double source, Double other) ->
	{
		return DEFAULT_DOUBLE_COMPARABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort chars in an descending order. 
	 */
	public static final IComparable<Character> 	INVERSE_CHAR_COMPERABLE
	= (Character source, Character other) ->
	{
		return DEFAULT_CHAR_COMPERABLE.compare(source, other).invert();
	};

	/**
	 * A comparator to sort Strings in an descending, alphabetic order. 
	 */
	public static final IComparable<String> 	
	INVERSE_ALPHABETICAL_STRING_COMPERABLE = (String source, String other) ->
	{
		return DEFAULT_ALPHABETICAL_STRING_COMPERABLE
								.compare(source, other).invert();
	};

	/**
	 * A comparator to sort Strings in an descending, alphabetic order. 
	 */
	public static final IComparable<String> 	INVERSE_SIZE_STRING_COMPERABLE 
	= (String source, String other) ->
	{
		return DEFAULT_SIZE_STRING_COMPERABLE
								.compare(source, other).invert();
	};
	
	/**
	 * This is the method that is being called to compared to instances.<br>
	 * <br>
	 * To make it work properly the method must stick to some 
	 * conventions (A and B are two instances of T, A being the parameter 
	 * 'source' and B being 'other'):<br>
	 * -If if does not matter, if the order is AB or BA, the method should 
	 * return {@link EComparisonResult}.EQUALS.<br>
	 * -If the order must be AB, the method should 
	 * return {@link EComparisonResult}.AFTER (Because A is after B).<br> 
	 * -If the order must be BA, the method should 
	 * return {@link EComparisonResult}.BEFORE (Because B is before A).<br> 
	 * (The terms before and after always refer to the place of 'other' 
	 * compared to 'source')
	 * 
	 * @param source The first element.
	 * @param other The second element.
	 * @return See above.
	 * 
	 * @see EComparisonResult
	 */
	public EComparisonResult compare(T source, T other);
	
	/**
	 * An enumeration to specify the result of a comparison. See 
	 * {@link IComparable} for further details.
	 * 
	 * @author 	Lukas Reichmann
	 * @version	1.0
	 * @see 	EComparisonResult
	 * 
	 */
	public enum EComparisonResult
	{
		/**
		 * The result, if the object A is meant to be before B
		 * (the equivalent to A &lt; B, when A and B are numbers).
		 */
		BEFORE,

		/**
		 * The result, if the object A is meant to be after B
		 * (the equivalent to A &gt; B, when A and B are numbers).
		 */
		AFTER,

		/**
		 * The result, if the objects are equal.
		 */
		EQUALS;
		
		/**
		 * Returns a new {@link EComparisonResult}, but with a inverted 
		 * value (BEVORE becomes AFTER, AFTER becomes BEVORE and EQUALS 
		 * stays EQUALS).
		 * 
		 * @return The inverted result.
		 */
		public EComparisonResult invert()
		{
			switch(this)
			{
				case BEFORE:
					return AFTER;
				case AFTER:
					return BEFORE;
				case EQUALS:
				default: //This case never happens.
					return EQUALS;
			}
		}
	}
}
