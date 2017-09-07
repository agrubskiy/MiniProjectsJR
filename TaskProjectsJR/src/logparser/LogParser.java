package logparser;

import logparser.query.*;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser implements IPQuery, UserQuery, DateQuery, EventQuery, QLQuery {

    private BufferedReader bufferedReader;
    private Path logPath;
    private List<DataFile> dataFileList = new ArrayList<>();

    public LogParser(Path logDir) throws IOException, ParseException {
        this.logPath = logDir;
        readLogFile();
    }

    /*
    *
    * IPQuery implementation
    *
    * */
    private class DataFile {

        private String IP;
        private String user;
        private Date date;
        private String userEvent;
        private Integer taskNumber;
        private String taskStatus;

        public DataFile(String IP, String user, Date date, String userEvent, Integer taskNumber, String taskStatus) {
            this.IP = IP;
            this.user = user;
            this.date = date;
            this.userEvent = userEvent;
            this.taskNumber = taskNumber;
            this.taskStatus = taskStatus;
        }

        public String getIP() {
            return IP;
        }

        public String getUser() {
            return user;
        }

        public Date getDate() {
            return date;
        }

        public String getUserEvent() {
            return userEvent;
        }

        public Integer getTaskNumber() {
            return taskNumber;
        }

        public String getTaskStatus() {
            return taskStatus;
        }
    }

    private void readLogFile() throws IOException, ParseException {

        String[] files = logPath.toFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        });

        for (String pathFile : files) {

            try {
                String s;
                bufferedReader = new BufferedReader(new FileReader(logPath.toFile().getAbsolutePath() + File.separator + pathFile));

                while ((s = bufferedReader.readLine())!= null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(s, "\t");
                    while (stringTokenizer.hasMoreElements()) {
                        String IP = stringTokenizer.nextElement().toString();
                        String user = stringTokenizer.nextElement().toString();
                        Date date = null;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
                        try {
                            date  = simpleDateFormat.parse(stringTokenizer.nextElement().toString());
                        }catch (ParseException e){

                        }
                        String userEvent  = stringTokenizer.nextElement().toString();
                        Integer taskNumber = null;
                        if (userEvent.contains(Event.SOLVE_TASK.toString()) || userEvent.contains(Event.DONE_TASK.toString())) {
                            StringTokenizer tokenizer = new StringTokenizer(userEvent, " ");
                            while (tokenizer.hasMoreElements()) {
                                userEvent = tokenizer.nextElement().toString();
                                taskNumber = Integer.parseInt(tokenizer.nextElement().toString());
                            }
                        }
                        String taskStatus  = stringTokenizer.nextElement().toString();

                        DataFile dataFile = new DataFile(IP, user, date, userEvent, taskNumber, taskStatus);
                        dataFileList.add(dataFile);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                bufferedReader.close();
            }

        }

        Comparator<DataFile> dataFileComparator = new Comparator<DataFile>() {
            @Override
            public int compare(DataFile o1, DataFile o2) {
                return o1.getDate().compareTo(o2.getDate()) > 0 ? 1 : 0;
            }
        };

        Collections.sort(dataFileList, dataFileComparator);

    }

    @Override
    public int getNumberOfUniqueIPs(Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                setIPs.add(dataFile.getIP());
        }

        return setIPs.size();
    }

    @Override
    public Set<String> getUniqueIPs(Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                setIPs.add(dataFile.getIP());
        }
        return setIPs;
    }

    public Set<String> getIPsForData(Date date, Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                if (dataFile.getDate().equals(date))
                    setIPs.add(dataFile.getIP());
        }

        return setIPs;

    }

    @Override
    public Set<String> getIPsForUser(String user, Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                if (dataFile.getUser().equals(user))
                    setIPs.add(dataFile.getIP());
        }

        return setIPs;
    }

    @Override
    public Set<String> getIPsForEvent(Event event, Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                if (after != null && before != null) {
                    if (dataFile.getDate().getTime() > after.getTime() && dataFile.getDate().getTime() < before.getTime())
                        if (dataFile.getUserEvent().equals(event.toString()))
                            setIPs.add(dataFile.getIP());
                }else {
                    if (dataFile.getUserEvent().equals(event.toString()))
                        setIPs.add(dataFile.getIP());
                }
        }
        return setIPs;
    }

    @Override
    public Set<String> getIPsForStatus(Status status, Date after, Date before) {

        Set<String> setIPs = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before))
                if (after != null && before != null) {
                    if (dataFile.getDate().getTime() > after.getTime() && dataFile.getDate().getTime() < before.getTime())
                        if (dataFile.getTaskStatus().equals(status.toString()))
                            setIPs.add(dataFile.getIP());
                }else {
                    if (dataFile.getTaskStatus().equals(status.toString()))
                        setIPs.add(dataFile.getIP());
                }
        }

        return setIPs;
    }

    public boolean isCompitableDate(long dateTime, Date after, Date before) {

        if (after == null && before == null){
            return true;
        }else if (after == null) {
            if (dateTime <= before.getTime()) {
                return true;
            }
        }else if (before == null) {
            if (dateTime >= after.getTime()){
                return true;
            }
        }else {
            if (dateTime >= after.getTime() && dateTime <= before.getTime()) {
                return true;
            }
        }

        return false;
    }

    /*
    * UserQuery implementation
    * */

    @Override
    public Set<String> getAllUsers() {
        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            users.add(dataFile.getUser());
        }

        return users;
    }

    @Override
    public int getNumberOfUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)){
                users.add(dataFile.getUser());
            }
        }

        return users.size();

    }

    public Set<String> getUsersForDate(Date date, Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)){
                if (dataFile.getDate().getTime() == date.getTime())
                    users.add(dataFile.getUser());
            }
        }

        return users;
    }

    @Override
    public int getNumberOfUserEvents(String user, Date after, Date before) {

        return getUserEvents(user, after, before).size();

    }

    public Set<String> getUserEvents(String user, Date after, Date before) {

        Set<String> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)) {
                    events.add(dataFile.getUserEvent());
                }
            }
        }
        return events;

    }

    public Set<String> getUserForEvents(Event event, Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(event.toString())) {
                    users.add(dataFile.getUser());
                }
            }
        }
        return users;

    }

    @Override
    public Set<String> getUsersForIP(String ip, Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getIP().equals(ip)){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getLoggedUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.LOGIN.toString())){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getDownloadedPluginUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.DOWNLOAD_PLUGIN.toString())){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getWroteMessageUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.WRITE_MESSAGE.toString())){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.SOLVE_TASK.toString())){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before, int task) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.SOLVE_TASK.toString())){
                    if (dataFile.getTaskNumber() != null && dataFile.getTaskNumber() == task)
                        users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.DONE_TASK.toString())
                        ){
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before, int task) {
        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.DONE_TASK.toString())
                        ){
                    if (dataFile.getTaskNumber()!=null && dataFile.getTaskNumber() == task)
                        users.add(dataFile.getUser());
                }
            }
        }

        return users;
    }

    public Set<String> getUserStatus(Status status, Date after, Date before) {

        Set<String> users = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskStatus().equals(status.toString())) {
                    users.add(dataFile.getUser());
                }
            }
        }

        return users;

    }
    /*
    *
    * DateQuery implementation
    *
    * */

    public Set<Date> getAllDates(Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {

                dates.add(dataFile.getDate());

            }
        }

        return dates;
    }

    @Override
    public Set<Date> getDatesForUserAndEvent(String user, Event event, Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user))
                    if (dataFile.getUserEvent().equals(event.toString())){
                        dates.add(dataFile.getDate());
                    }
            }
        }

        return dates;
    }

    public Set<Date> getDatesForIPs(String ip, Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getIP().equals(ip))
                    dates.add(dataFile.getDate());
            }
        }

        return dates;
    }

    public Set<Date> getDatesForUsers(String user, Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user))
                    dates.add(dataFile.getDate());
            }
        }

        return dates;
    }

    public Set<Date> getDatesForStatus(Status status, Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskStatus().equals(status.toString()))
                    dates.add(dataFile.getDate());
            }
        }

        return dates;
    }

    public Set<Date> getDatesForEvent(Event event, Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getDate().getTime() > after.getTime() && dataFile.getDate().getTime() < before.getTime())
                    if (dataFile.getUserEvent().equals(event.toString()))
                        dates.add(dataFile.getDate());
            }
        }

        return dates;
    }

    @Override
    public Set<Date> getDatesWhenSomethingFailed(Date after, Date before) {

        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskStatus().equals(Status.FAILED.toString())){
                    dates.add(dataFile.getDate());
                }
            }
        }

        return dates;
    }

    @Override
    public Set<Date> getDatesWhenErrorHappened(Date after, Date before) {
        Set<Date> dates = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskStatus().equals(Status.ERROR.toString())){
                    dates.add(dataFile.getDate());
                }
            }
        }

        return dates;
    }

    @Override
    public Date getDateWhenUserLoggedFirstTime(String user, Date after, Date before) {

        Set<Date> dates = new TreeSet<>();
        Date date = null;

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)){
                    if (dataFile.getUserEvent().equals(Event.LOGIN.toString()))
                        dates.add(dataFile.getDate());
                }
            }
        }

        if (dates.size() != 0) {
            date = (Date) dates.toArray()[0];
        }

        return date;

    }

    @Override
    public Date getDateWhenUserSolvedTask(String user, int task, Date after, Date before) {

        Set<Date> dates = new TreeSet<>();
        Date date = null;

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)){
                    if (dataFile.getUserEvent().equals(Event.SOLVE_TASK.toString()))
                        if (dataFile.getTaskNumber() != null && dataFile.getTaskNumber() == task)
                            dates.add(dataFile.getDate());
                }
            }
        }

        if (dates.size() != 0) {
            date = (Date) dates.toArray()[0];
        }

        return date;

    }

    @Override
    public Date getDateWhenUserDoneTask(String user, int task, Date after, Date before) {

        Set<Date> dates = new TreeSet<>();
        Date date = null;

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)){
                    if (dataFile.getUserEvent().equals(Event.DONE_TASK.toString()))
                        if (dataFile.getTaskNumber() != null && dataFile.getTaskNumber() == task)
                            dates.add(dataFile.getDate());
                }
            }
        }

        if (dates.size() != 0) {
            date = (Date) dates.toArray()[0];
        }

        return date;

    }

    @Override
    public Set<Date> getDatesWhenUserWroteMessage(String user, Date after, Date before) {

        Set<Date> dates = new TreeSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)){
                    if (dataFile.getUserEvent().equals(Event.WRITE_MESSAGE.toString()))
                        dates.add(dataFile.getDate());
                }
            }
        }

        return dates;
    }

    @Override
    public Set<Date> getDatesWhenUserDownloadedPlugin(String user, Date after, Date before) {

        Set<Date> dates = new TreeSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user)){
                    if (dataFile.getUserEvent().equals(Event.DOWNLOAD_PLUGIN.toString()))
                        dates.add(dataFile.getDate());
                }
            }
        }

        return dates;

    }

    /*
    *
    * EventQuery implementation
    *
    * */

    @Override
    public int getNumberOfAllEvents(Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent() != null) {
                    events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events.size();
    }

    @Override
    public Set<Event> getAllEvents(Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent()!= null) {
                    events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events;
    }

    @Override
    public Set<Event> getEventsForIP(String ip, Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent()!= null) {
                    if (dataFile.getIP().equals(ip))
                        events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events;
    }

    @Override
    public Set<Event> getEventsForUser(String user, Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent()!= null) {
                    if (dataFile.getUser().equals(user))
                        events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events;
    }

    public Set<Event> getEventsForDate(Date date, Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)){
                if (dataFile.getDate().getTime() == date.getTime())
                    events.add(Event.valueOf(dataFile.getUserEvent()));
            }
        }

        return events;
    }

    public Set<Event> getEventsForStatus(Status status, Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)){
                if (dataFile.getTaskStatus().equals(status.toString()))
                    events.add(Event.valueOf(dataFile.getUserEvent()));
            }
        }

        return events;
    }

    @Override
    public Set<Event> getFailedEvents(Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent()!= null) {
                    Status status = Status.valueOf(dataFile.getTaskStatus());
                    if (status.equals(Status.FAILED))
                        events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events;
    }

    @Override
    public Set<Event> getErrorEvents(Date after, Date before) {

        Set<Event> events = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent()!= null) {
                    Status status = Status.valueOf(dataFile.getTaskStatus());
                    if (status.equals(Status.ERROR))
                        events.add(Event.valueOf(dataFile.getUserEvent()));
                }
            }
        }

        return events;
    }

    @Override
    public int getNumberOfAttemptToSolveTask(int task, Date after, Date before) {

        int attempt = 0;

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.SOLVE_TASK.toString())) {
                    if (dataFile.getTaskNumber() != null && dataFile.getTaskNumber() == task)
                        attempt++;
                }
            }
        }

        return attempt;
    }

    @Override
    public int getNumberOfSuccessfulAttemptToSolveTask(int task, Date after, Date before) {

        int attempt = 0;

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(Event.DONE_TASK.toString())) {
                    if (dataFile.getTaskNumber() != null && dataFile.getTaskNumber() == task) {
                        attempt++;
                    }
                }
            }
        }

        return attempt;
    }

    @Override
    public Map<Integer, Integer> getAllSolvedTasksAndTheirNumber(Date after, Date before) {

        Map<Integer, Integer> map = new HashMap<>();
        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskNumber() != null && dataFile.getUserEvent().equals(Event.SOLVE_TASK.toString())) {
                    Integer i = dataFile.getTaskNumber();
                    if (map.containsKey(i)) {
                        map.put(dataFile.getTaskNumber(), map.get(dataFile.getTaskNumber()) + 1);
                    }else {
                        map.put(dataFile.getTaskNumber(), 1);
                    }

                }
            }
        }

        return map;

    }

    @Override
    public Map<Integer, Integer> getAllDoneTasksAndTheirNumber(Date after, Date before) {

        Map<Integer, Integer> map = new HashMap<>();
        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getTaskNumber() != null && dataFile.getUserEvent().equals(Event.DONE_TASK.toString())) {
                    Integer i = dataFile.getTaskNumber();
                    if (map.containsKey(i)) {
                        map.put(dataFile.getTaskNumber(), map.get(dataFile.getTaskNumber()) + 1);
                    }else {
                        map.put(dataFile.getTaskNumber(), 1);
                    }

                }
            }
        }

        return map;
    }

    public Set<Status> getAllStatus(Date after, Date before) {

        Set<Status> statuses = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                statuses.add(Status.valueOf(dataFile.getTaskStatus()));
            }
        }

        return statuses;
    }

    public Set<Status> getStatusForIPs(String ip, Date after, Date before) {

        Set<Status> statuses = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getIP().equals(ip))
                    statuses.add(Status.valueOf(dataFile.getTaskStatus()));
            }
        }

        return statuses;
    }

    public Set<Status> getStatusForUsers(String user, Date after, Date before) {

        Set<Status> statuses = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUser().equals(user))
                    statuses.add(Status.valueOf(dataFile.getTaskStatus()));
            }
        }

        return statuses;
    }

    public Set<Status> getStatusForDate(Date date, Date after, Date before) {

        Set<Status> statuses = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getDate().getTime() == date.getTime())
                    statuses.add(Status.valueOf(dataFile.getTaskStatus()));
            }
        }

        return statuses;
    }

    public Set<Status> getStatusForEvent(Event event, Date after, Date before) {

        Set<Status> statuses = new HashSet<>();

        for (DataFile dataFile : dataFileList) {
            if (isCompitableDate(dataFile.getDate().getTime(), after, before)) {
                if (dataFile.getUserEvent().equals(event.toString()))
                    statuses.add(Status.valueOf(dataFile.getTaskStatus()));
            }
        }

        return statuses;
    }

    /*
    *
    * QLQuery implementation
    *
    * */

    @Override
    public Set<Object> execute(String query) {

        Set<Object> objectSet = new HashSet<>();

        if (query == null || query.isEmpty())
            return objectSet;

        Pattern pattern = Pattern.compile("get (ip|user|date|event|status)"
                + "( for (ip|user|date|event|status) = \"(.*?)\")?"
                + "( and date between \"(.*?)\" and \"(.*?)\")?");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {

            String field1 = matcher.group(1);
            String field2 = matcher.group(3);
            String value1 = matcher.group(4);
            String value6 = matcher.group(6);
            String value7  = matcher.group(7);

            Date before = null;
            Date after = null;

            try {
                after = parseDateFormat(value6);
                before = parseDateFormat(value7);
            }catch (Exception e) {

            }

            if (field1.equals("ip")){

                if (field2 != null) {
                    switch (field2) {
                        case "user" : objectSet.addAll(getIPsForUser(value1, after, before));
                            break;
                        case "date" : objectSet.addAll(getIPsForData(parseDateFormat(value1), after, before));
                            break;
                        case "event" : objectSet.addAll(getIPsForEvent(Event.valueOf(value1), after, before));
                            break;
                        case "status" : objectSet.addAll(getIPsForStatus(Status.valueOf(value1), after, before));
                            break;
                    }
                }else {
                    objectSet.addAll(getUniqueIPs(null, null));
                }
            }else if (field1.equals("user")) {

                if (field2 != null) {
                    switch (field2) {
                        case "ip" : objectSet.addAll(getUsersForIP(value1, after, before));
                            break;
                        case "date" : objectSet.addAll(getUsersForDate(parseDateFormat(value1), after, before));
                            break;
                        case "event" : objectSet.addAll(getUserForEvents(Event.valueOf(value1), after, before));
                            break;
                        case "status" : objectSet.addAll(getUserStatus(Status.valueOf(value1), after, before));
                            break;
                    }
                }else {
                    objectSet.addAll(getAllUsers());
                }
            }else if (field1.equals("date")) {
                if (field2 != null) {
                    switch (field2) {
                        case "ip" : objectSet.addAll(getDatesForIPs(value1, after, before));
                            break;
                        case "user" : objectSet.addAll(getDatesForUsers(value1, after, before));
                            break;
                        case "event" : objectSet.addAll(getDatesForEvent(Event.valueOf(value1), after, before));
                            break;
                        case "status" : objectSet.addAll(getDatesForStatus(Status.valueOf(value1), after, before));
                            break;
                    }
                }else {
                    objectSet.addAll(getAllDates(null, null));
                }
            }else if (field1.equals("event")) {
                if (field2 != null) {
                    switch (field2) {
                        case "ip" : objectSet.addAll(getEventsForIP(value1, after, before));
                            break;
                        case "date" : objectSet.addAll(getEventsForDate(parseDateFormat(value1), after, before));
                            break;
                        case "user" : objectSet.addAll(getEventsForUser(value1, after, before));
                            break;
                        case "status" : objectSet.addAll(getEventsForStatus(Status.valueOf(value1), after, before));
                            break;
                    }
                }else {
                    objectSet.addAll(getAllEvents(null, null));
                }
            }else if (field1.equals("status")) {
                if (field2 != null) {
                    switch (field2) {
                        case "ip" : objectSet.addAll(getStatusForIPs(value1, after, before));
                            break;
                        case "date" : objectSet.addAll(getStatusForDate(parseDateFormat(value1), after, before));
                            break;
                        case "event" : objectSet.addAll(getStatusForEvent(Event.valueOf(value1), after, before));
                            break;
                        case "user" : objectSet.addAll(getStatusForUsers(value1, after, before));
                            break;
                    }
                }else {
                    objectSet.addAll(getAllStatus(null, null));
                }
            }else {
                return objectSet;
            }

        }



        return objectSet;
    }

    public Date parseDateFormat(String source) {

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

        try {
            date = simpleDateFormat.parse(source);
        }catch (ParseException e) {

        }

        return date;
    }
}