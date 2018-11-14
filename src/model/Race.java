package model;

import java.util.*;
import java.util.stream.Collectors;

import model.track.Track;

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Myles Haynes
 */
public class Race {

	private final Track track;
	private final int numLaps;
	private final int avgLapTime;
	private final int numRacers;
	private int time;
	private Random rng;
	private Map<Participant, Integer> lastMessageTime;
	private final int granularity;

	private List<Participant> racers;

	public Race(Track track, int numLaps, int numRacers, int avgLapTime, int telemetryInterval) {
		this.track = track;
		this.numLaps = numLaps;
		this.numRacers = numRacers;
		this.avgLapTime = avgLapTime * 1000;
		this.time = 0;
		this.granularity = telemetryInterval;
		lastMessageTime = new HashMap<>();
		rng = new Random();
		racers = buildRacers();
	}

	public List<String> stepRace() {

		List<String> messages = new ArrayList<>();
		if (time == 0) {
			messages.addAll(setUpMessages());
		}
		for (Participant participant : racers) {
			participant.step(track.getSpeedClass(participant.getDistance()));

			if (lastMessageTime.get(participant) % granularity == 0) {
				messages.add(format("$T:%d:%s:%.2f:%d", time, participant.getRacerId(), participant.getDistance(), participant.getLapNum()));
			}
			lastMessageTime.compute(participant, (_r, i) -> i + 1);
		}
		newLeaderBoard().ifPresent(messages::add);
		messages.addAll(crossingMessages());
		time++;
		return messages;
	}

	private List<String> setUpMessages() {
		return racers.stream().map(r -> "#" + r.getRacerId() + ":" + r.getName() + ":" + r.getDistance())
				.collect(Collectors.toList());

	}

	public boolean stillGoing() {
		return racers.stream().map(Participant::getLapNum).anyMatch((l) -> l < numLaps);
	}

	private Optional<String> newLeaderBoard() {
		List<Participant> prevPlaces = new ArrayList<>(racers);
		sort(racers);
		if (prevPlaces.equals(racers)) {
			return Optional.empty();
		} else {
			String leaderBoard = "$L:" + time + ":";
			leaderBoard += racers.stream().map(Participant::getRacerId).map(Object::toString).collect(joining(":"));
			return Optional.of(leaderBoard);
		}
	}

	private List<String> crossingMessages() {

		List<String> messages = new ArrayList<>();
		for (Participant r : racers) {
			if (r.timeUntilCrossingFinish() < 1) {
				double crossTime = time + r.timeUntilCrossingFinish();
				int newLap = r.getLapNum() + 1;
				messages.add(format("$C:%.2f:%s:%d:%b", crossTime, r.getRacerId(), newLap, newLap == numLaps));
			}
		}
		return messages;

	}

	private List<Participant> buildRacers() {

		if (numRacers > 100) {
			throw new UnsupportedOperationException("Cannot create more than 100 racers currently.");
		}
		List<Participant> racers = new ArrayList<>();

		double speed = track.getTrackLength() * 1.0 / avgLapTime;
		double minSpeed = speed * 0.98;
		double maxSpeed = speed * 1.02;

		HashSet<Integer> usedIds = new HashSet<>();
		while (usedIds.size() < numRacers) {
			usedIds.add(rng.nextInt(100));
		}
		int i = 0;
		for (int id : usedIds) {
			int startDistance = round(track.getTrackLength() * (i * .01f));
			Participant racer = new Participant(id, startDistance, track.getTrackLength(), minSpeed, maxSpeed);
			lastMessageTime.put(racer, i);
			racers.add(racer);
			i--;
		}
		return racers;
	}

}
