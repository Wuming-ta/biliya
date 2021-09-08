package com.jfeat.observer;

/**
 * Created by jacky on 4/15/16.
 */
public class SubjectEvent {
    private Class<? extends Subject> subjectClass;
    private int event;

    public SubjectEvent() {

    }

    public SubjectEvent(Class<? extends Subject> subjectClass, int event) {
        this.subjectClass = subjectClass;
        this.event = event;
    }

    public Class<? extends Subject> getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(Class<? extends Subject> subjectClass) {
        this.subjectClass = subjectClass;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((subjectClass == null) ? 0 : subjectClass.hashCode());
        result = PRIME * result + event;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SubjectEvent other = (SubjectEvent) obj;
        if (subjectClass == null) {
            if (other.subjectClass != null) {
                return false;
            }
        } else if (!subjectClass.equals(other.subjectClass)) {
            return false;
        }
        if (event != other.event) {
            return false;
        }
        return true;
    }
}
