package com.kakao.tech;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FruitBasket {
	public static void main(String[] args) throws InterruptedException {
		final List<String> basket1 = Arrays.asList(new String[] { "kiwi", "orange", "lemon", "orange", "lemon", "kiwi" });
		final List<String> basket2 = Arrays.asList(new String[] { "banana", "lemon", "lemon", "kiwi" });
		final List<String> basket3 = Arrays.asList(new String[] { "strawberry", "orange", "lemon", "grape", "strawberry" });
		final List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
		final Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

		basketFlux.concatMap(basket -> {
			final Mono<List<String>> distinctFruits = Flux.fromIterable(basket).distinct().collectList();
			final Mono<Map<String, Long>> countFruitMono = Flux.fromIterable(basket)
				.groupBy(fruit -> fruit)
				.concatMap(groupedFlux -> groupedFlux.count()
					.map(count -> {
						final Map<String, Long> fruitCount = new LinkedHashMap<>();
						fruitCount.put(groupedFlux.key(), count);
						return fruitCount;
					})
				)
				.reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() {{
					putAll(accumulatedMap);
					putAll(currentMap);
				}});

			return Flux.zip(distinctFruits, countFruitMono, (distinct, count) -> new FruitInfo(distinct, count));
		}).subscribe(System.out::println);
	}
}