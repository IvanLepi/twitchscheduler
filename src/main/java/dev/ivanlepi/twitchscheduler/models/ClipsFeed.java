package dev.ivanlepi.twitchscheduler.models;
import java.util.List;

import lombok.Data;

@Data
public class ClipsFeed {
    private List<Clip> data;
}
