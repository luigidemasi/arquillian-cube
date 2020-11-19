package org.arquillian.cube.openshift.standalone;

import java.io.IOException;
import java.net.URL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.arquillian.cube.openshift.api.Template;
import org.arquillian.cube.openshift.api.TemplateParameter;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.arquillian.cube.openshift.impl.requirement.RequiresOpenshift;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

// tag::openshift_template_example[]
@Category(RequiresOpenshift.class)
@RequiresOpenshift
@RunWith(ArquillianConditionalRunner.class)
@Template(url = "classpath:hello-openshift.yaml",
          parameters = @TemplateParameter(name = "RESPONSE", value = "Hello from Arquillian Template"))
public class HelloWorldTemplateIT {

    @RouteURL("hello-openshift-route")
    @AwaitRoute
    private URL url;

    @Test
    public void should_create_class_template_resources() throws IOException {
        verifyResponse(url);
    }

    @Test
    @Template(url = "https://gist.githubusercontent.com/luigidemasi/fada1398fc585a4db681c79e756dd907/raw/a7c263a5ab2cd2559944eaecf1fcba4809ec8b38/hello_openshift_route_template.yaml",
            parameters = @TemplateParameter(name = "ROUTE_NAME", value = "hello-openshift-method-route"))
    public void should_create_method_template_resources(
        @RouteURL("hello-openshift-method-route") @AwaitRoute URL routeUrl)
        throws IOException {
        verifyResponse(routeUrl);
    }

    private void verifyResponse(URL url) throws IOException {
        assertThat(url).isNotNull();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Response response = okHttpClient.newCall(request).execute();

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("Hello from Arquillian Template\n");
    }
}
// end::openshift_template_example[]
