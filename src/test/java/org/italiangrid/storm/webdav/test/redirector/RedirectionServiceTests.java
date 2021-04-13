/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2014-2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.storm.webdav.test.redirector;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.italiangrid.storm.webdav.config.ServiceConfigurationProperties;
import org.italiangrid.storm.webdav.oauth.authzserver.ResourceAccessTokenRequest;
import org.italiangrid.storm.webdav.oauth.authzserver.ResourceAccessTokenRequest.Permission;
import org.italiangrid.storm.webdav.oauth.authzserver.jwt.SignedJwtTokenIssuer;
import org.italiangrid.storm.webdav.redirector.DefaultRedirectionService;
import org.italiangrid.storm.webdav.redirector.RedirectError;
import org.italiangrid.storm.webdav.redirector.ReplicaSelector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import com.nimbusds.jwt.SignedJWT;

@RunWith(MockitoJUnitRunner.class)
public class RedirectionServiceTests extends RedirectorTestSupport {


  @Mock
  Authentication authentication;

  @Mock
  HttpServletRequest request;

  @Mock
  HttpServletResponse response;

  @Mock
  SignedJwtTokenIssuer tokenIssuer;

  @Mock
  SignedJWT token;

  @Mock
  ReplicaSelector selector;

  @Captor
  ArgumentCaptor<ResourceAccessTokenRequest> tokenRequest =
      ArgumentCaptor.forClass(ResourceAccessTokenRequest.class);


  ServiceConfigurationProperties config;
  DefaultRedirectionService service;

  @Before
  public void setup() {
    config = buildConfigurationProperties();
    when(tokenIssuer.createResourceAccessToken(Mockito.any(), Mockito.any())).thenReturn(token);
    when(token.serialize()).thenReturn(RANDOM_TOKEN_STRING);
    when(selector.selectReplica()).thenReturn(Optional.empty());
    when(request.getServletPath()).thenReturn(PATH);
    when(request.getMethod()).thenReturn("GET");
    service = new DefaultRedirectionService(config, tokenIssuer, selector);
  }


  @Test(expected = RedirectError.class)
  public void testRedirectFailureOnEmptyReplica() {

    try {
      service.buildRedirect(authentication, request, response);
    } catch (RedirectError e) {
      assertThat(e.getMessage(), containsString("No replica found"));
      throw e;
    }

  }

  @Test
  public void testRedirectUriConstruction() {
    when(selector.selectReplica()).thenReturn(Optional.of(REPLICA_0));

    String uriString = service.buildRedirect(authentication, request, response);
    verify(tokenIssuer).createResourceAccessToken(tokenRequest.capture(), Mockito.any());


    URI uri = URI.create(uriString);
    
    assertThat(uri.getHost(), is(URI_0_HOST));
    assertThat(uri.getScheme(), is(URI_0_SCHEME));
    assertThat(uri.getPath(), is(PATH));
    assertThat(uri.getQuery(), is(ACCESS_TOKEN_QUERY_STRING));
    assertThat(tokenRequest.getValue().getPath(), is(PATH));
    assertThat(tokenRequest.getValue().getPermission(), is(Permission.r));
    assertThat(tokenRequest.getValue().getLifetimeSecs(),
        is(config.getRedirector().getMaxTokenLifetimeSecs()));
    
  }

  @Test
  public void testPutRedirectUriConstruction() {
    when(selector.selectReplica()).thenReturn(Optional.of(REPLICA_0));
    when(request.getMethod()).thenReturn("PUT");

    String uriString = service.buildRedirect(authentication, request, response);
    verify(tokenIssuer).createResourceAccessToken(tokenRequest.capture(), Mockito.any());


    URI uri = URI.create(uriString);

    assertThat(uri.getHost(), is(URI_0_HOST));
    assertThat(uri.getScheme(), is(URI_0_SCHEME));
    assertThat(uri.getPath(), is(PATH));
    assertThat(uri.getQuery(), is(ACCESS_TOKEN_QUERY_STRING));
    assertThat(tokenRequest.getValue().getPath(), is(PATH));
    assertThat(tokenRequest.getValue().getPermission(), is(Permission.rw));
    assertThat(tokenRequest.getValue().getLifetimeSecs(),
        is(config.getRedirector().getMaxTokenLifetimeSecs()));

  }

  @Test
  public void testRedirectUriWithPrefixConstruction() {
    when(selector.selectReplica()).thenReturn(Optional.of(REPLICA_WITH_PREFIX));

    String uriString = service.buildRedirect(authentication, request, response);

    URI uri = URI.create(uriString);

    assertThat(uri.getHost(), is(URI_WITH_PREFIX_HOST));
    assertThat(uri.getScheme(), is(URI_WITH_PREFIX_SCHEME));
    assertThat(uri.getPath(), is(PATH_WITH_PREFIX));
    assertThat(uri.getQuery(), is(ACCESS_TOKEN_QUERY_STRING));

  }


}
