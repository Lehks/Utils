package container.sorting;

import container.SortedArray;
import container.sorting.IComparable.EComparisonResult;

/**
 * A class that is used to search in an {@link SortedArray} for an element with 
 * a certain trait (which is defined by the user). This interface is used by 
 * <code>{@link SortedArray}.find(ISearchCondition, T1)</code><br>
 * This is used to compare e.g. the returned value of a method in the type that
 * is stored in the {@link SortedArray} against another value (that is passed 
 * to the ISearchCondition though the parameter 'customObj'). The 
 * {@link SortedArray} calls <code>.isCorrectElement(...)</code> with every 
 * element in the array until it return true.<br>
 * <br>
 * Example:<br>
 * 'Person' represents a Person that has an unique ID (which is returned by 
 * <code>.getId()</code> and is an Integer).
 * 'array' is a {@link SortedArray} of type 'Person' that is already filled 
 * with some references to different Persons.<br>
 * <br><pre><code>
array.find(new ISearchCondition&lt;Person, Integer&gt;()
{
	{@literal @}Override
	public boolean isCorrectElement(Person element, Integer customObj)
	{
		/{@literal *}
		 {@literal *} The ID that was given to .find(...) (1234) was passed to 
		 {@literal *} .isCorrectElement(...).
		 {@literal *}/
		return element.getId() == customObj;
	}
}, 1234);
 * </code></pre><br>
 * <br>
 * In this case, the {@link SortedArray} tried to find the Person with the ID
 * 1234.
 * 
 * @author 	Lukas Reichmann
 * @version 1.0
 * @see 	SortedArray
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
	 * @return			True, if 'element' is the correct one, false if not.
	 */
	public EComparisonResult isCorrectElement(T element, T1 customObj);
	
	public default boolean canSearchBinary()
	{
		return false;
	}
}
