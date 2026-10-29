package com.interntrack.users;

public class ProfileResponse {
    private User user;
    private StudentProfile studentProfile;
    private MentorProfile mentorProfile;

    public ProfileResponse() {}

    public ProfileResponse(User user, StudentProfile studentProfile, MentorProfile mentorProfile) {
        this.user = user;
        this.studentProfile = studentProfile;
        this.mentorProfile = mentorProfile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }

    public MentorProfile getMentorProfile() {
        return mentorProfile;
    }

    public void setMentorProfile(MentorProfile mentorProfile) {
        this.mentorProfile = mentorProfile;
    }
}
