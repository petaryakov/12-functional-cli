package ohm.softa.a12.icndb.suppliers;

import ohm.softa.a12.icndb.ICNDBApi;
import ohm.softa.a12.icndb.ICNDBService;
import ohm.softa.a12.model.JokeDto;
import ohm.softa.a12.model.ResponseWrapper;
import org.apache.commons.lang3.NotImplementedException;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Supplier implementation to retrieve all jokes of the ICNDB in a linear way
 * @author Peter Kurfer
 */

public final class AllJokesSupplier implements Supplier<ResponseWrapper<JokeDto>> {

    /* ICNDB API proxy to retrieve jokes */
    private final ICNDBApi icndbApi;

	/**
	 * total count of jokes
	 */
	private int jokesCount;

	/**
	 * count of already retrieved jokes, required to determine when counter has to be reset
	 */
	private int retrievedJokes;


	private int latestId;
    public AllJokesSupplier() {
        icndbApi = ICNDBService.getInstance();
        /* TODO fetch the total count of jokes the API is aware of
         * to determine when all jokes are iterated and the counters have to be reset */
		retrievedJokes = 0;
		latestId = 0;

		try {
			jokesCount = icndbApi.getJokeCount().get().getValue();
		} catch (InterruptedException | ExecutionException e) {
			/* fallback - not the best kind of solution here */
			jokesCount = 0;
		}
    }

    public ResponseWrapper<JokeDto> get() {
        /* TODO retrieve the next joke
         * note that there might be IDs that are not present in the database
         * you have to catch an exception and continue if no joke was retrieved to an ID
         * if you retrieved all jokes (count how many jokes you successfully fetched from the API)
         * reset the counters and continue at the beginning */
        //throw new NotImplementedException("Method `get()` is not implemented");
		/* if fallback value return null to indicate that no jokes can be retrieved */
		if(jokesCount == 0) return null;
		ResponseWrapper<JokeDto> retrievedJoke;
		/* try ro retrieve a joke until it succeeds */
		do {
			try {
				/* if all jokes were retrieved - reset counters */
				if(retrievedJokes >= jokesCount) {
					latestId = 0;
					retrievedJokes = 0;
				}
				/* fetch joke with a blocking future */
				retrievedJoke = icndbApi.getJoke(++latestId).get();
				retrievedJokes++;
			} catch (InterruptedException | ExecutionException e) {
				retrievedJoke = null;
			}
		}while (retrievedJoke == null);
		return retrievedJoke;
    }

}
