package container.sorting;

import container.SortedArray;
import container.sorting.IComparable.EComparisonResult;

/**
 * A class that allows to search though a {@link SortedArray} using the method
 * <code>.find(ISearchCondition, T1)</code> without needing an instance of the 
 * object should be found. {@link SortedArray} passes an element to 
 * <code>.isCorrectElement(...)</code> that it is currently checking 
 * if it is the right element. It also passes a custom object to method. This
 * custom object was passed by the caller of <code>.find(ISearchCondition, T1)
 * </code>. This custom object is meant to be used to be compared against
 * values in the element.<br>
 * <br>
 * Example:<br>
 * In this example, '<code>Person</code>' is a class that holds information 
 * about a person's name (which can be retrieved with <code>.getName()</code>) 
 * and each instance has an ID assigned to it (which can be retrieved with 
 * <code>.getID()</code>).
 * Furthermore, '<code>array</code>' is an instance of {@link SortedArray} that
 * holds references to instances of <code>Person</code>. The array is sorted
 * after the IDs in an increasing order.<br>
 * <pre><code>
array.find(new ISearchCondition&lt;Person, String&gt;()
{
	{@literal @}Override
	public EComparisonResult isCorrectElement(Person element,
			String customObj)
	{
		return (element.getName() == customObj ? 
				EComparisonResult.EQUAL : EComparisonResult.UNEQUAL);
	}
}, "Josh Meyer");
 * </code></pre>
 * In this case, <code>.find(...)</code> would search for first Person in
 * the {@link SortedArray} with the name "Josh Meyer" sequentially. When 
 * searching sequentially, <code>.isCorrectElement()</code> may only two
 * values: {@link EComparisonResult}.EQUAL, if the currently checked element
 * is the correct one, {@link EComparisonResult}.UNEQAL, if not.<br>
 * <br>
 * Since this searching is done sequentially, but in some special cases, this 
 * searching can be done binary. In the following example, the 
 * {@link ISearchCondition} is configured in a way, that it searches for a
 * person with an specific ID (which can be done binary, because the array is
 * sorted after the IDs).<br>
 * <pre><code>
array.find(new ISearchCondition&lt;Person, Integer&gt;()
{
	{@literal @}Override
	public EComparisonResult isCorrectElement(Person element,
			Integer customObj)
	{
		if(customObj.equals(element.getID()))
			return EComparisonResult.EQUAL;
		else if(customObj &gt; element.getID())
			return EComparisonResult.AFTER;
		else
			return EComparisonResult.BEFORE;
	}
}, 1234);
 * </code></pre>
 * In this case, the {@link ISearchCondition} works very much like an 
 * {@link IComparable} and returns {@link EComparisonResult}.EQUAL, if the 
 * element is the correct one and {@link EComparisonResult}.BEFORE or 
 * {@link EComparisonResult}.AFTER, depending on the element to find relative
 * to the currently checked element.
 * 
 * @author 	Lukas Reichmann
 * @version	1.0
 * @see		SortedArray
 * @see		EComparisonResult
 *
 * @param <T>	The type of the elements in the {@link SortedArray}.
 * @param <T1>	The type of the custom object.
 */





public interface ISearchCondition<T, T1>
{
	/**
	 * The method that the interface is about.
	 * 
	 * @param element	The element that is currently checked if it is the 
	 * 					right one.
	 * @param customObj	An Object that can be used to get necessary information
	 * 					into this method.
	 * @return			In the case that searching is done sequentially: 
	 * 					{@link EComparisonResult}.EQUAL, if 'element' is the 
	 * 					correct one, {@link EComparisonResult}.UNEQUAL if not;
	 * 					In the case that searching is done sequentially: 
	 * 					{@link EComparisonResult}.EQUAL, if 'element' is the 
	 * 					correct one, {@link EComparisonResult}.BEFORE, if the
	 * 					correct element is before the current element and
	 * 					{@link EComparisonResult}.AFTER if it is after the
	 * 					current element.
	 */
	public EComparisonResult isCorrectElement(T element, T1 customObj);
	
	/**
	 * If this returns true, <code>SortedArray.find(ISearchCondition, T1)
	 * </code> can search binary. Note that this only works if <code>
	 * .isCorrectElement(...)</code> supports this operation. If not, said 
	 * <code>.find(...)</code> will most likely return a wrong result.<br>
	 * <br>
	 * The default implementation does nothing but returning <code>false
	 * </code>. Overriding this method makes only sense, if <code>true</code>
	 * should be returned.
	 * 
	 * @return 	True, if <code>.isCorrectElement(...)</code> allows it to 
	 * 			search binary, false if not.
	 */
	public default boolean canSearchBinary()
	{
		return false;
	}
}
