package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

public class Part05MergeTest {
	private Part05Merge workshop = new Part05Merge();

	final static User MARIE = new User("mschrader", "Marie", "Schrader");
	final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");

	ReactiveRepository<User> repositoryWithDelay = new ReactiveUserRepository(500);
	ReactiveRepository<User> repository          = new ReactiveUserRepository(MARIE, MIKE);

	@Test
	public void mergeWithInterleave() {
		final Flux<User> flux = workshop.mergeFluxWithInterleave(repositoryWithDelay.findAll(), repository.findAll());

		StepVerifier.create(flux)
			.expectNext(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
			.verifyComplete();
	}

	@Test
	public void mergeWithNoInterleave() {
		final Flux<User> flux = workshop.mergeFluxWithNoInterleave(repositoryWithDelay.findAll(), repository.findAll());

		StepVerifier.create(flux)
			.expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
			.verifyComplete();
	}

	@Test
	public void multipleMonoToFlux() {
		final Mono<User> skylerMono = repositoryWithDelay.findFirst();
		final Mono<User> marieMono = repository.findFirst();
		final Flux<User> flux = workshop.createFluxFromMultipleMono(skylerMono, marieMono);

		StepVerifier.create(flux)
			.expectNext(User.SKYLER, MARIE)
			.verifyComplete();
	}
}