package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Part04TransformTest {
	private Part04Transform workshop = new Part04Transform();
	private ReactiveRepository<User> repository = new ReactiveUserRepository();

	@Test
	public void transformMono() {
		final Mono<User> mono = repository.findFirst();

		StepVerifier.create(workshop.capitalizeOne(mono))
			.expectNext(new User("SWHITE", "SKYLER", "WHITE"))
			.verifyComplete();
	}

	@Test
	public void transformFlux() {
		final Flux<User> flux = repository.findAll();
		
		StepVerifier.create(workshop.capitalizeMany(flux))
			.expectNext(
				new User("SWHITE", "SKYLER", "WHITE"),
				new User("JPINKMAN", "JESSE", "PINKMAN"),
				new User("WWHITE", "WALTER", "WHITE"),
				new User("SGOODMAN", "SAUL", "GOODMAN"))
			.verifyComplete();
	}

	@Test
	public void asyncTransformFlux() {
		final Flux<User> flux = repository.findAll();

		StepVerifier.create(workshop.asyncCapitalizeMany(flux))
			.expectNext(
				new User("SWHITE", "SKYLER", "WHITE"),
				new User("JPINKMAN", "JESSE", "PINKMAN"),
				new User("WWHITE", "WALTER", "WHITE"),
				new User("SGOODMAN", "SAUL", "GOODMAN"))
			.verifyComplete();
	}
}