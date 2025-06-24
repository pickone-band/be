package com.pickone.global.oauth2.model.domain;

public enum OAuth2Provider {
  GOOGLE, SPOTIFY, SOUNDCLOUD, INSTAGRAM;

  public static OAuth2Provider from(String providerName) {
    return OAuth2Provider.valueOf(providerName.toUpperCase());
  }
}