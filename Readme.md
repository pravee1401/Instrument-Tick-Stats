
This services provides APIs to record ticks for various instruments and gives various statistics as below.

Count - Count of all the instrument ticks recorded / Count of individual instrument ticks
Max - Maximum price of all the instrument ticks recorded / Maximum price of individual instrument ticks
Min - Minimum price of all the instrument ticks recorded / Minimum price of individual instrument ticks
Avg - Average price of all the instrument ticks recorded / Average price of individual instrument ticks

The API specification is as per the problem statement and is as below.

	POST /ticks
		Every time a new tick arrives, this endpoint will be called. Body:
		{
		"instrument": "IBM.N",
		"price": 143.82,
		"timestamp": 1478192204000
		}
		where:
		• instrument - a financial instrument identifier (string; list of instruments is not known to our service in
		advance so we add them dynamically)
		• price - current trade price of a financial instrument (double)
		• timestamp - tick timestamp in milliseconds (long; this is not current timestamp)
		Returns: Empty body with either 201 or 204:
		• 201 - in case of success
		• 204 - if tick is older than 60 seconds
	
	GET /statistics
		This is the endpoint with aggregated statistics for all ticks across all instruments, this endpoint has to
		execute in constant time and memory (O(1)).
		It returns the following statistics based on the ticks which happened in the last 60 seconds (sliding time
		interval).
		Returns:
		
		{
		"avg": 100,
		"max": 200,
		"min": 50,
		"count": 10
		}
		where:
		• avg is a double specifying the average amount of all tick prices in the last 60 seconds
		• max is a double specifying single highest tick price in the last 60 seconds
		• min is a double specifying single lowest tick price in the last 60 seconds
		• count is a long specifying the total number of ticks happened in the last 60 seconds
	
	GET /statistics/{instrument_identifier}
		This is the endpoint with statistics for a given instrument.
		It returns the statistic based on the ticks with a given instrument identifier happened in the last 60 seconds
		(sliding time interval). The response is the same as for the previous endpoint but with instrument specific
		statistics.


How to run:

	It can be run as a spring boot application.
	
	Swagger ui is enabled and the apis can be used/consumed by using URL http://localhost:8080/swagger-ui/index.html

Assumptions:

	There will be one tick per instrument per second. This means there will be no duplicate ticks for any instrument for the same second.
	In case of duplicate ticks, they are ignored in case of individual instrument statistics but overall statistics will take that into account.

Features/Highlights:

	Ordering of ticks doesn't matter and it will work for any order and parallelly.
	
	Tried to avoid synchronization at most places by using atomic constructs at most places for faster performance,
	while still maintaining data consistency and avoiding data race conditions. This is achieved by using complex atomic data structures and 
	performing atomic operations instead of doing operations in a synchronized block.
	
Further Improvements:

	Currently the statistics will keep getting updated until the ticks for the corresponding instruments are flowing into the system.
	The moment the ticks stop flowing, the statistics are not updated. This can be implemented in service by introducing dummy ticks for that 
	second, if there are no actual ticks. This requires slight modifications in the runnable tasks as well.
	
	Further improvising or simplifying the code to eliminate synchronization altogether.
	
******
The challenge was quite a bit of challenge :D. I thought of not using synchronization at all and only use atomic constructs and hence the solution 
has become little complex, by the use of complex data structures. But it will achieve faster performance. 

With these constructs in place vertical scaling is also possible along with horizontal scaling.











