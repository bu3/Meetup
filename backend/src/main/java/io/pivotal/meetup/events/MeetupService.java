package io.pivotal.meetup.events;

import com.meetup.api.Event;
import com.meetup.api.MeetupClient;
import com.meetup.api.OpenEventsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MeetupService {

    @Autowired
    private MeetupClient meetupClient;

    public List<Meetup> findMeetups(FindMeetupsRequest request) {
        OpenEventsResult openEventsResult =
                request.getState() == null ? meetupClient.findOpenEvents(request.getCity(), request.getCountryCode())
                : meetupClient.findOpenEvents(request.getCity(), request.getState(), request.getCountryCode());
        return convert(openEventsResult);
    }

    public Meetup findMeetup(String urlName, String eventId) {
        return convert(meetupClient.findEvent(urlName, eventId));
    }

    private List<Meetup> convert(OpenEventsResult openEventsResult) {
        if (isEmptyResult(openEventsResult)) {
            return null;
        }

        List<Meetup> meetups = new ArrayList<>();
        openEventsResult.getResults().forEach(event -> meetups.add(convert(event)));
        return meetups;
    }

    private boolean isEmptyResult(OpenEventsResult openEventsResult) {
        return openEventsResult.getMeta().getTotalCount() == 0;
    }

    private Meetup convert(Event event) {

        if (isNullEvent(event)) {
            return null;
        }

        Meetup meetup = new Meetup();
        meetup.setId(event.getId());
        meetup.setName(event.getName());
        meetup.setDescription(event.getDescription());
        meetup.setTime(event.getTime());

        if (event.getGroup() != null) {
            meetup.setUrlName(event.getGroup().getUrlName());
        }
        return meetup;
    }

    private boolean isNullEvent(Event event) {
        return event == null;
    }
}
