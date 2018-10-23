package com.siyka.omron.fins.master;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Futures {

	
	public static void main(String... arguments) throws InterruptedException, ExecutionException {
		final List<CompletableFuture<String>> futures = new ArrayList<>();
		
		IntStream.range(0,  5)
			.forEach(i -> futures.add(CompletableFuture.supplyAsync(() -> {
				final Instant startTime = Instant.now();
				final int sleepyTime = (int) (10 * Math.random());
				try {
					TimeUnit.SECONDS.sleep(sleepyTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				return String.format("Start: %s, Finish: %s, SleepyTime: %d", startTime, Instant.now(), sleepyTime);
			})));
		
		CompletableFuture<List<String>> instants = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenApply(v -> {
			return futures.stream()
					.map(future -> future.join())
					.collect(Collectors.toList());
		});
		
		instants.get().forEach(System.out::println);
	}
	
}



