/**
 * Contains implementations of data adapters that directly store their modeled data in mutable lists or maps instead of delegating to an
 * application data model.
 * <p>
 * Note that implementing application specific adapters delegating to its data model should be the preferred way for performance reasons.
 * It should be avoided to copy large amounts of data from the application business model to instances of the classes provided here.
 * These classes are meant to be used, e.g., if stored data is not part of an application data model (so it cannot be stored there) but must 
 * still be processed. 
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 */
package info.bioinfweb.jphyloio.dataadapters.implementations.store;