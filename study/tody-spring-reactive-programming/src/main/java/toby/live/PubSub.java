package toby.live;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TOBY TV 6회 - 스프링 리액티브 웹 개발 (2) - Operators
 *
 * Reactive Streams - Operators
 *
 * Publisher -> [Data1] -> Op1 -> [Data2] -> Op2 -> [Data3] -> Subscriber
 *
 * 1. map (d1 -> f -> d2)
 *
 * pub -> [Data1] -> mapPub -> [Data2] -> logSub
 *                <- subscribe(logSub)
 *                -> onSubscribe(s)
 *                -> onNext
 *                -> onNext
 *                -> onComplete
 * @see Publisher
 * @see Subscriber
 * @see BiFunction
 */
@Slf4j
public class PubSub {
	public static void main(String[] args) {
		Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
//		Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
		Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//		Publisher<List> mapPub = mapPub(pub, Collections::singletonList);
//		Publisher<Integer> sumPub = sumPub(pub);
//		Publisher<Integer> reducePub = reducePub(pub, 0, (a, b)->a+b);
		mapPub.subscribe(logSub());
	}

	// 1,2,3,4,5,
	// 0 -> (0,1) -> 0 + 1 = 1
	// 1 -> (1,2) -> 1 + 2 = 3
	// 3 -> (3,3) -> 3 + 3 = 6
	// 6 -> (6,4) -> 6 + 4 = 10
	// 10 -> (10,5) -> 10 + 5 = 15

//	private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init, BiFunction<Integer, Integer, Integer> bf) {
//		return new Publisher<Integer>() {
//			@Override
//			public void subscribe(Subscriber<? super Integer> sub) {
//
//				pub.subscribe(new DelegateSub(sub) {
//					int result = init;
//
//					@Override
//					public void onNext(Integer i) {
//						result = bf.apply(result, i);
//					}
//
//					@Override
//					public void onComplete() {
//						sub.onNext(result);
//						sub.onComplete();
//					}
//				});
//			}
//		};
//	}

//	private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//		return new Publisher<Integer>() {
//			@Override
//			public void subscribe(Subscriber<? super Integer> sub) {
//				pub.subscribe(new DelegateSub(sub) {
//					int sum = 0;
//
//					@Override
//					public void onNext(Integer i) {
//						sum += i;
//					}
//
//					@Override
//					public void onComplete() {
//						sub.onNext(sum);
//						sub.onComplete();
//					}
//				});
//
//			}
//		};
//	}

//	T -> R
	private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
		return new Publisher<R>() {
			@Override
			public void subscribe(Subscriber<? super R> sub) {
				pub.subscribe(new DelegateSub<T, R>(sub) {
					@Override
					public void onNext(T i) {
						sub.onNext(f.apply(i));
					}
				});
			}
		};
	}

	private static <T> Subscriber<T> logSub() {
		return new Subscriber<T>() {
			@Override
			public void onSubscribe(Subscription s) {
				log.debug("onSubscribe:");
				s.request(Long.MAX_VALUE);

			}

			@Override
			public void onNext(T i) {
				log.debug("onNext : {}", i);
			}

			@Override
			public void onError(Throwable t) {
				log.debug("onError : {}", t);
			}

			@Override
			public void onComplete() {
				log.debug("onComplete");
			}
		};
	}

	private static Publisher<Integer> iterPub(List<Integer> iter) {
		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> sub) {
				sub.onSubscribe(new Subscription() {
					@Override
					public void request(long n) {
						try {
							iter.forEach(s->sub.onNext(s));
							sub.onComplete();
						} catch(Throwable t) {
							sub.onError(t);
						}
					}

					@Override
					public void cancel() {

					}
				});
			}
		};
	}
}
