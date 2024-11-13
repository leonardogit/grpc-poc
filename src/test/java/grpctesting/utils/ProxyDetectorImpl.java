package grpctesting.utils;

import io.grpc.HttpConnectProxiedSocketAddress;
import io.grpc.ProxiedSocketAddress;
import io.grpc.ProxyDetector;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class ProxyDetectorImpl implements ProxyDetector {

    private final ProxySelector proxySelector;

    public ProxyDetectorImpl(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    @Override
    public ProxiedSocketAddress proxyFor(SocketAddress targetAddress) throws IOException {

        if (!(targetAddress instanceof InetSocketAddress)) {
            return null;
        }

        InetSocketAddress inetSocketAddress = (InetSocketAddress) targetAddress;
        URI uri = URI.create(
                "http://" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort()
        );
        List<Proxy> proxies = proxySelector.select(uri);
        if (proxies == null || proxies.isEmpty()) {
            return null;
        }
        Proxy proxy = proxies.get(0);
        if (proxy.type() == Proxy.Type.DIRECT) {
            return null;
        }
        InetSocketAddress proxyAddress = (InetSocketAddress) proxy.address();
        return HttpConnectProxiedSocketAddress.newBuilder()
                .setTargetAddress(inetSocketAddress)
                .setProxyAddress(proxyAddress)
                .build();
    }

}
