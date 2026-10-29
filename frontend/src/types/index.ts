export type UserRole = 'STUDENT' | 'MENTOR' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'SUSPENDED' | 'DELETED';

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
  avatarUrl?: string;
  status: UserStatus;
  createdAt: string;
  updatedAt?: string;
}

export interface StudentProfile {
  userId: number;
  collegeName?: string;
  degree?: string;
  branch?: string;
  graduationYear?: number;
  cgpa?: number;
  skillsJson?: string;
  interestsJson?: string;
  preferredRolesJson?: string;
  preferredLocationsJson?: string;
  portfolioUrl?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  profileCompletionPct?: number;
}

export interface MentorProfile {
  userId: number;
  collegeName?: string;
  graduatingBatch?: number;
  fieldOfWork?: string;
  currentCompany?: string;
  currentRole?: string;
  yearsOfExperience?: number;
  skillsJson?: string;
  bio?: string;
  linkedinUrl?: string;
  openForReferrals?: boolean;
  isVerifiedMentor?: boolean;
  fullName?: string;
  email?: string;
  avatarUrl?: string;
}

export interface AuthResponse {
  token: string | null;
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
  avatarUrl?: string;
  profileCompletionPct?: number;
}

export type ApplicationStatus =
  | 'SAVED'
  | 'APPLIED'
  | 'ASSESSMENT'
  | 'INTERVIEW'
  | 'OFFER_RECEIVED'
  | 'REJECTED'
  | 'WITHDRAWN';

export interface Application {
  id: number;
  userId: number;
  opportunityId?: number;
  companyName: string;
  role: string;
  jobDescription?: string;
  sourceUrl?: string;
  stipendSalary?: string;
  location?: string;
  employmentType?: string;
  status: ApplicationStatus;
  appliedDate?: string;
  deadline?: string;
  interviewDate?: string;
  assessmentDate?: string;
  followUpDate?: string;
  notes?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface ApplicationStatusHistory {
  id: number;
  applicationId: number;
  previousStatus?: string;
  newStatus: string;
  notes?: string;
  changedAt: string;
}

export interface ApplicationReminder {
  id: number;
  userId: number;
  applicationId: number;
  reminderType: 'ASSESSMENT' | 'INTERVIEW' | 'FOLLOW_UP' | 'DEADLINE';
  reminderDate: string;
  notes?: string;
  isDismissed: boolean;
  companyName?: string;
  role?: string;
}

export interface ApplicationStats {
  totalApplications: number;
  inProgressCount: number;
  assessmentsScheduled: number;
  interviewsScheduled: number;
  offersReceived: number;
  upcomingDeadlines: number;
  conversionRate: number;
  statusDistribution: Record<string, number>;
  applicationsOverTime: Array<{ month: string; count: number }>;
}

export interface Opportunity {
  id: number;
  title: string;
  companyName: string;
  description: string;
  requiredSkillsJson?: string;
  eligibleDegreesJson?: string;
  eligibleBranchesJson?: string;
  minCgpa?: number;
  eligibleBatchesJson?: string;
  stipendSalary?: string;
  location?: string;
  workplaceType?: 'ON_SITE' | 'HYBRID' | 'REMOTE';
  employmentType?: 'INTERNSHIP' | 'FULL_TIME' | 'CO_OP';
  deadline?: string;
  applyUrl?: string;
  source?: string;
  verificationStatus: 'VERIFIED' | 'EXTERNAL' | 'UNVERIFIED';
  isActive: boolean;
  createdBy?: number;
  createdAt: string;
  isSaved?: boolean;
  hasApplied?: boolean;
}

export interface EligibilityResult {
  eligible: boolean;
  cgpaEligible: boolean;
  degreeEligible: boolean;
  branchEligible: boolean;
  batchEligible: boolean;
  passedCriteria: string[];
  failedCriteria: string[];
  summary: string;
}

export interface ResumeFile {
  id: number;
  userId: number;
  fileName: string;
  fileSize: number;
  mimeType: string;
  uploadedAt: string;
}

export interface ResumeFinding {
  id?: number;
  sectionName: string;
  findingType: 'STRENGTH' | 'IMPROVEMENT' | 'MISSING' | 'WARNING';
  title: string;
  description: string;
  suggestion?: string;
}

export interface ResumeAnalysis {
  id: number;
  userId: number;
  resumeFileId: number;
  jobTitle?: string;
  jobDescription?: string;
  completenessScore: number;
  matchPercentage: number;
  matchedSkillsJson?: string;
  missingSkillsJson?: string;
  providerType: string;
  summary: string;
  createdAt: string;
  findings: ResumeFinding[];
  resumeFileName?: string;
}

export interface ScamFinding {
  id?: number;
  indicatorName: string;
  riskWeight: number;
  evidenceSnippet?: string;
  explanation: string;
  recommendation?: string;
}

export interface ScamScan {
  id: number;
  userId: number;
  companyName?: string;
  jobTitle?: string;
  recruiterContact?: string;
  applicationUrl?: string;
  stipendSalary?: string;
  jobDescription?: string;
  recruiterMessage?: string;
  overallRiskLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  riskScore: number;
  providerType: string;
  isReported: boolean;
  createdAt: string;
  findings: ScamFinding[];
  recommendedSteps?: string[];
}

export interface CommunityPost {
  id: number;
  authorId: number;
  title: string;
  category: string;
  companyName?: string;
  roleTitle?: string;
  content: string;
  upvoteCount: number;
  commentCount: number;
  isPinned: boolean;
  status: 'PUBLISHED' | 'FLAGGED' | 'REMOVED';
  createdAt: string;
  updatedAt?: string;
  authorName?: string;
  authorRole?: string;
  authorCompany?: string;
  authorAvatarUrl?: string;
  isVerifiedMentor?: boolean;
  hasUpvoted?: boolean;
  isSaved?: boolean;
}

export interface PostComment {
  id: number;
  postId: number;
  authorId: number;
  content: string;
  createdAt: string;
  authorName?: string;
  authorRole?: string;
  authorAvatarUrl?: string;
}

export interface ReferralRequest {
  id: number;
  studentId: number;
  mentorId: number;
  opportunityTitle: string;
  companyName: string;
  resumeFileId?: number;
  note?: string;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'CLOSED';
  mentorResponse?: string;
  createdAt: string;
  updatedAt?: string;
  studentName?: string;
  studentEmail?: string;
  studentAvatarUrl?: string;
  mentorName?: string;
  mentorCompany?: string;
  mentorRole?: string;
  resumeFileName?: string;
}

export interface AdminMetrics {
  totalStudents: number;
  totalMentors: number;
  totalActiveOpportunities: number;
  totalApplicationsRecorded: number;
  pendingContentReports: number;
  pendingOpportunityReports: number;
  reportedScamScans: number;
}

export interface ContentReport {
  id: number;
  reporterId: number;
  targetType: string;
  targetId: number;
  reason: string;
  status: string;
  resolutionNotes?: string;
  createdAt: string;
  reporterName?: string;
  reporterEmail?: string;
  targetTitle?: string;
}

export interface OpportunityReport {
  id: number;
  opportunityId: number;
  reporterId: number;
  reason: string;
  status: string;
  createdAt: string;
  opportunityTitle?: string;
  companyName?: string;
  reporterName?: string;
  reporterEmail?: string;
}

export interface AuditLog {
  id: number;
  adminId: number;
  action: string;
  targetType?: string;
  targetId?: number;
  details?: string;
  ipAddress?: string;
  createdAt: string;
  adminName?: string;
  adminEmail?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
}
