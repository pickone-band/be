package com.pickone.global.music.mapper;

import com.pickone.global.music.dto.MusicInfoResponse;
import com.pickone.global.music.dto.SocialMusicTrackResponse;
import com.pickone.global.music.model.vo.MusicInfo;
import org.springframework.stereotype.Component;

@Component
public class MusicMapper {

  public MusicInfo toVo(MusicInfoResponse response) {
    if (response == null) return null;
    return MusicInfo.of(
        response.title(),
        response.artist(),
        response.album(),
        response.genre(),
        response.platformTrackId()
    );
  }

  public MusicInfoResponse toResponse(MusicInfo vo) {
    if (vo == null) return null;
    return new MusicInfoResponse(
        vo.getTitle(),
        vo.getArtist(),
        vo.getAlbum(),
        vo.getGenre(),
        vo.getPlatformTrackId()
    );
  }

  public MusicInfoResponse toResponseFromSocialTrack(SocialMusicTrackResponse track) {
    if (track == null) return null;
    return new MusicInfoResponse(
        track.title(),
        track.artist(),
        track.album(),
        track.genre(),
        track.platformTrackId()
    );
  }
}
