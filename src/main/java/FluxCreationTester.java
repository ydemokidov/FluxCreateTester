import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Date;

public class FluxCreationTester {

    public static void main(String[] args) {

        long millisStart = new Date().getTime();

        Flux<String> flux1 = Flux.create(fluxSink -> {
            for (int i=0;i<1000;i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fluxSink.next(String.valueOf(i));
            }
            fluxSink.complete();
        });

        Flux<String> flux2 = Flux.create(fluxSink -> {
            for (int i=0;i<1000;i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fluxSink.next(String.valueOf(i));
            }
            fluxSink.complete();
        });

        //параллельное выполнение
        Flux.zip(flux1
                .subscribeOn(Schedulers.parallel()),
                flux2.subscribeOn(Schedulers.parallel()))
                .doOnNext(tuple -> System.out.println(Thread.currentThread().getName() + ": " + tuple.getT1() + ", " + tuple.getT2()))
                .blockLast();

        //последовательное выполнение в один поток
        //Flux.zip(flux1,flux2).doOnNext(tuple -> System.out.println(Thread.currentThread().getName() + ": " + tuple.getT1() + ", " + tuple.getT2())).blockLast();

        long millisEnd = new Date().getTime();

        System.out.println("Time taken: "+(millisEnd-millisStart));

    }

}
