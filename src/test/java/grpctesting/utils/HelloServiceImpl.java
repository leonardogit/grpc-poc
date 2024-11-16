package grpctesting.utils;

import hello.Hello;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import hello.HelloServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HelloServiceImpl {

    private ManagedChannel channel;
    private HelloServiceGrpc.HelloServiceStub asyncStub;

    @Before
    public void setUp() {
        // Conecta ao servidor remoto gRPC
        channel = ManagedChannelBuilder.forAddress("grpcbin.test.k6.io", 9001)
                .usePlaintext() // Desabilita SSL/TLS para testes locais
                .build();

        // Inicializa o stub assíncrono
        asyncStub = HelloServiceGrpc.newStub(channel);

        // Verifique se o canal foi inicializado corretamente
        if (channel == null) {
            throw new IllegalStateException("Falha ao inicializar o canal gRPC");
        }
    }

    @After
    public void tearDown() {
        // Encerra o canal após os testes
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Erro ao encerrar o canal  " + e.getMessage());
            }
        }
    }

    @Test
    public void testSayHello() throws InterruptedException{
        // Cria um CountDownLatch para aguardar a resposta assíncrona
        CountDownLatch latch = new CountDownLatch(1);

        // Verifica se o stub foi inicializado corretamente
        if (asyncStub == null) {
            fail("O stub gRPC assíncrono não foi inicializado.");
        }

        // Cria a requisição com o campo "greeting" definido como "Bert"
        Hello.HelloRequest request = Hello.HelloRequest.newBuilder()
                .setGreeting("Bert")
                .build();

        //Faz a chamada assíncrona ao método SayHello
        asyncStub.sayHello(request, new StreamObserver<Hello.HelloResponse>() {
            @Override
            public void onNext(Hello.HelloResponse helloResponse) {
                //Recebe a resposta do servidor
                assertEquals("hello Bert" , helloResponse.getReply());
            }

            @Override
            public void onError(Throwable throwable) {
                // Captura exceções de tempo limite ou outros erros de comunicação
                fail("Falha na chamada gRPC: " + throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                //Finaliza a comunicação
                System.out.println("Comunicação concluída com sucesso .");
                latch.countDown();
            }
        });
        if (!latch.await(5,TimeUnit.SECONDS)){
            fail("Timeout na chamada GRPC .");
        }

    }
}