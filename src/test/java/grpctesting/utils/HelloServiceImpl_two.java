package grpctesting.utils;

import hello.Hello;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelloServiceImpl_two {

    private ManagedChannel channel;
    private hello.HelloServiceGrpc.HelloServiceBlockingStub blockingStub;

    @BeforeEach
    public void setUp() {
        // Cria o canal de comunicação com o servidor gRPC
        channel = ManagedChannelBuilder.forAddress("grpcbin.test.k6.io", 9001)
                .usePlaintext() // Desabilita SSL/TLS para testes
                .build();

        // Cria o stub para chamadas síncronas (blocking)
        blockingStub = hello.HelloServiceGrpc.newBlockingStub(channel);
    }

    @After
    public void tearDown() throws InterruptedException {
        // Fecha o canal de comunicação
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testSayHello() {
        // Cria a requisição com a mensagem de saudação
        Hello.HelloRequest request = Hello.HelloRequest.newBuilder()
                .setGreeting("Teste")
                .build();

        Hello.HelloResponse response;
        try {
            // Faz a chamada ao método SayHello do serviço gRPC
            response = blockingStub.sayHello(request);

            // Verifica se a resposta não é nula
            assertNotNull(response, "A resposta não deve ser nula");

            // Verifica se o status da resposta é OK
            assertEquals("Hello", response.getReply().substring(0, 5), "A resposta deve conter 'Hello'");

            // Verifica o conteúdo da resposta
            System.out.println("Resposta do servidor: " + response.getReply());

        } catch (StatusRuntimeException e) {
            fail("A chamada RPC falhou: " + e.getStatus());
        }
    }

}
