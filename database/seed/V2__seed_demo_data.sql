-- InternTrack: Demonstration Seed Data
-- Note: All sample passwords below are hashed with BCrypt for 'password123'
-- Except Admin which is 'Admin@123' (hash: $2a$10$K4rB90L5z8aPz5r591YkC.F7v7M3vK0a9c4X8v7z6B5c4d3e2f1g2)
-- Password for all seed accounts: password123 (hash: $2a$10$GGL7H2p.Yf6z1ZkO/jS9m.OevGZvh7h7rZ7vK.E8aLp1bK0vN4c2m)
-- (Application auto-updates or verifies with Spring Security BCryptPasswordEncoder)

-- Insert Demo Users
INSERT INTO users (id, email, password_hash, full_name, role, status) VALUES
(1, 'admin@interntrack.edu', '$2a$10$eACCYoNOHEqgkVE8aI.EWe7O.Y/w/N6M5s4zQfK7a8uQe71sYj3aO', 'System Administrator', 'ADMIN', 'ACTIVE'),
(2, 'priya.sharma@alumni.edu', '$2a$10$eACCYoNOHEqgkVE8aI.EWe7O.Y/w/N6M5s4zQfK7a8uQe71sYj3aO', 'Priya Sharma', 'MENTOR', 'ACTIVE'),
(3, 'rohit.verma@alumni.edu', '$2a$10$eACCYoNOHEqgkVE8aI.EWe7O.Y/w/N6M5s4zQfK7a8uQe71sYj3aO', 'Rohit Verma', 'MENTOR', 'ACTIVE'),
(4, 'aravind.nair@student.edu', '$2a$10$eACCYoNOHEqgkVE8aI.EWe7O.Y/w/N6M5s4zQfK7a8uQe71sYj3aO', 'Aravind Nair', 'STUDENT', 'ACTIVE'),
(5, 'sneha.patel@student.edu', '$2a$10$eACCYoNOHEqgkVE8aI.EWe7O.Y/w/N6M5s4zQfK7a8uQe71sYj3aO', 'Sneha Patel', 'STUDENT', 'ACTIVE')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Insert Student Profiles
INSERT INTO student_profiles (user_id, college_name, degree, branch, graduation_year, cgpa, skills_json, interests_json, preferred_roles_json, preferred_locations_json, portfolio_url, github_url, linkedin_url, profile_completion_pct) VALUES
(4, 'National Institute of Technology', 'B.Tech', 'Computer Science and Engineering', 2026, 8.85, '["Java", "Spring Boot", "React", "TypeScript", "MySQL", "Docker", "Data Structures"]', '["Cloud Computing", "Full-stack Development", "Distributed Systems"]', '["Software Development Engineer Intern", "Backend Engineer"]', '["Bengaluru", "Hyderabad", "Remote"]', 'https://aravindnair.dev', 'https://github.com/aravindnair', 'https://linkedin.com/in/aravindnair', 90),
(5, 'Indian Institute of Information Technology', 'B.Tech', 'Information Technology', 2026, 8.40, '["Python", "Machine Learning", "FastAPI", "React", "PostgreSQL", "Pandas"]', '["Artificial Intelligence", "Data Engineering", "NLP"]', '["AI/ML Engineer Intern", "Data Scientist Intern"]', '["Pune", "Bengaluru", "Remote"]', 'https://snehapatel.me', 'https://github.com/snehapatel', 'https://linkedin.com/in/snehapatel', 85)
ON DUPLICATE KEY UPDATE college_name=VALUES(college_name);

-- Insert Mentor Profiles
INSERT INTO mentor_profiles (user_id, college_name, graduating_batch, field_of_work, current_company, current_role, years_of_experience, skills_json, bio, linkedin_url, open_for_referrals, is_verified_mentor) VALUES
(2, 'National Institute of Technology', 2022, 'Software Engineering', 'Microsoft', 'Software Development Engineer II', 3, '["C#", "Distributed Systems", "Azure", "Microservices", "System Design"]', 'Alumni from 2022 batch. Passionate about helping juniors crack tech interviews, system design rounds, and resume building.', 'https://linkedin.com/in/priyasharma-tech', TRUE, TRUE),
(3, 'Indian Institute of Information Technology', 2021, 'Machine Learning', 'Adobe', 'Member of Technical Staff', 4, '["Python", "PyTorch", "Computer Vision", "LLMs", "C++"]', 'Working on generative creative tooling. Happy to mentor students on AI/ML roles and share interview questions.', 'https://linkedin.com/in/rohitverma-ai', TRUE, TRUE)
ON DUPLICATE KEY UPDATE current_company=VALUES(current_company);

-- Insert Opportunities
INSERT INTO opportunities (id, title, company_name, description, required_skills_json, eligible_degrees_json, eligible_branches_json, min_cgpa, eligible_batches_json, stipend_salary, location, workplace_type, employment_type, deadline, apply_url, source, verification_status, is_active, created_by) VALUES
(1, 'Software Engineer Intern (Summer 2026)', 'Microsoft', 'Join the core cloud infrastructure and Azure team to design high-throughput microservices and resilient distributed systems. You will collaborate with senior architects and contribute to production cloud services.', '["Java", "C++", "Data Structures", "Algorithms", "System Design basics"]', '["B.Tech", "M.Tech", "Dual Degree"]', '["Computer Science and Engineering", "Information Technology", "Electronics"]', 7.50, '[2026, 2027]', 'INR 1,25,000 / month', 'Bengaluru / Hyderabad', 'HYBRID', 'INTERNSHIP', '2026-12-15', 'https://careers.microsoft.com', 'OFFICIAL_PORTAL', 'VERIFIED', TRUE, 1),
(2, 'Backend Developer Intern', 'Razorpay', 'We are looking for enthusiastic engineering interns to build secure payment flows, merchant analytics APIs, and transaction processing pipelines with high availability.', '["Go", "Java", "MySQL", "REST APIs", "Redis"]', '["B.Tech", "B.E.", "MCA"]', '["Computer Science and Engineering", "Information Technology"]', 7.00, '[2026]', 'INR 45,000 / month', 'Bengaluru', 'ON_SITE', 'INTERNSHIP', '2026-11-30', 'https://razorpay.com/jobs', 'CAMPUS', 'VERIFIED', TRUE, 1),
(3, 'AI/ML Research Intern', 'Adobe', 'Work alongside Adobe Research scientists on multimodal generative modeling, intelligent canvas editing algorithms, and efficient deep learning inference.', '["Python", "PyTorch", "Deep Learning", "NumPy", "Math & Linear Algebra"]', '["B.Tech", "M.Tech", "MS"]', '["Computer Science and Engineering", "Data Science", "AI/ML"]', 8.00, '[2026, 2027]', 'INR 1,00,000 / month', 'Noida / Bengaluru', 'HYBRID', 'INTERNSHIP', '2026-12-01', 'https://adobe.wd5.myworkdayjobs.com', 'OFFICIAL_PORTAL', 'VERIFIED', TRUE, 1),
(4, 'Frontend Engineer Intern', 'Zerodha', 'Build blazing fast, accessible financial web applications using modern web standards, TypeScript, and reactive architectures with minimalist design.', '["JavaScript", "TypeScript", "React", "CSS", "Web Performance"]', '["B.Tech", "BCA", "MCA", "B.Sc"]', '["Any Engineering / Computing Branch"]', 6.50, '[2025, 2026]', 'INR 50,000 / month', 'Bengaluru', 'REMOTE', 'INTERNSHIP', '2026-11-20', 'https://zerodha.com/careers', 'OFFICIAL_PORTAL', 'VERIFIED', TRUE, 1),
(5, 'Graduate Trainee Engineer - 2026 Batch', 'Cisco Systems', 'Explore enterprise networking, distributed routing, software-defined WAN, and enterprise cybersecurity systems.', '["Python", "Networking Basics", "Linux", "C", "TCP/IP"]', '["B.Tech", "M.Tech"]', '["Computer Science and Engineering", "Electronics and Communication", "Information Technology"]', 7.00, '[2026]', 'INR 14,00,000 / annum', 'Bengaluru', 'HYBRID', 'FULL_TIME', '2026-12-31', 'https://jobs.cisco.com', 'CAMPUS', 'VERIFIED', TRUE, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert Demo Applications for Student 4
INSERT INTO applications (id, user_id, opportunity_id, company_name, role, job_description, source_url, stipend_salary, location, employment_type, status, applied_date, deadline, interview_date, assessment_date, notes) VALUES
(1, 4, 1, 'Microsoft', 'Software Engineer Intern', 'Core cloud platform internship focusing on scalable backend systems.', 'https://careers.microsoft.com', 'INR 1,25,000 / month', 'Hyderabad', 'INTERNSHIP', 'INTERVIEW', '2026-10-01', '2026-12-15', '2026-10-25 14:00:00', '2026-10-10 10:00:00', 'Cleared OA with 100% test cases. Technical round 1 scheduled on Microsoft Teams.'),
(2, 4, 2, 'Razorpay', 'Backend Developer Intern', 'Payment processing and merchant APIs team.', 'https://razorpay.com/jobs', 'INR 45,000 / month', 'Bengaluru', 'INTERNSHIP', 'ASSESSMENT', '2026-10-05', '2026-11-30', NULL, '2026-10-22 18:00:00', 'Online assessment link received on HackerRank. Covers DSA & SQL queries.'),
(3, 4, 4, 'Zerodha', 'Frontend Engineer Intern', 'High-performance web dashboard engineering.', 'https://zerodha.com/careers', 'INR 50,000 / month', 'Remote', 'INTERNSHIP', 'APPLIED', '2026-10-12', '2026-11-20', NULL, NULL, 'Applied via career portal with personal portfolio and GitHub open source links.'),
(4, 4, NULL, 'Atlassian', 'Software Engineer Intern', 'Jira Cloud platform engineering team.', 'https://atlassian.com/careers', 'INR 1,10,000 / month', 'Bengaluru', 'INTERNSHIP', 'SAVED', NULL, '2026-11-15', NULL, NULL, 'Need to review Confluence architecture and practice Graph algorithms before applying.')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name);

-- Insert Application Status History
INSERT INTO application_status_history (application_id, previous_status, new_status, notes) VALUES
(1, 'APPLIED', 'ASSESSMENT', 'Received online assessment invitation on Codility.'),
(1, 'ASSESSMENT', 'INTERVIEW', 'Passed assessment; scheduled 45-minute virtual interview with engineering manager.'),
(2, 'APPLIED', 'ASSESSMENT', 'HackerRank test link sent.')
ON DUPLICATE KEY UPDATE notes=VALUES(notes);

-- Insert Application Reminders
INSERT INTO application_reminders (user_id, application_id, reminder_type, reminder_date, notes, is_dismissed) VALUES
(4, 1, 'INTERVIEW', '2026-10-25 13:30:00', 'Microsoft Round 1: Prepare Trees, Graphs, and System Design concepts.', FALSE),
(4, 2, 'ASSESSMENT', '2026-10-22 17:30:00', 'Razorpay HackerRank test link expires tonight.', FALSE)
ON DUPLICATE KEY UPDATE notes=VALUES(notes);

-- Insert Community Posts
INSERT INTO community_posts (id, author_id, title, category, company_name, role_title, content, upvote_count, comment_count, is_pinned, status) VALUES
(1, 2, 'Cracking Microsoft Summer Internship: Complete 4-Round Roadmap & Questions', 'Software Development', 'Microsoft', 'Software Engineer Intern', 'Hello juniors! Having been through the on-campus drive at NIT, here is the exact breakdown of what to expect:\n\n1. Online Assessment (Codility):\n- 3 questions in 90 minutes. Focus on Sliding Window, Dynamic Programming on grids, and Trie prefix searches.\n\n2. Round 1 (Technical & DSA):\n- In-depth discussion on projects. Asked to implement LRU Cache with thread safety considerations.\n- Follow up on binary search over monotonic answer spaces.\n\n3. Round 2 (System Design Basics & Low-Level Design):\n- Designed a rate limiter algorithm (Token Bucket vs Leaky Bucket).\n- Discussed database indexing strategies (B-Tree vs Hash index) for read-heavy workloads.\n\n4. Round 3 (AA / Behavioral Round):\n- Why Microsoft? Tell me about a time you had a technical disagreement with a teammate.\n\nTop Tip: Write clean, modular code with descriptive variable names. Explain your thought process out loud before writing any code!', 38, 4, TRUE, 'PUBLISHED'),
(2, 3, 'How to Stand Out for AI/ML Internships in 2026 Without 5 Publications', 'Data Science and AI/ML', 'Adobe', 'AI/ML Intern', 'A lot of students think you need top-tier CVPR/NeurIPS papers to land a high-paying AI internship. While research papers help, solid engineering implementation matters just as much.\n\nWhat recruiters and interviewers look for:\n1. End-to-end reproducible projects: Don''t just run a Jupyter Notebook. Build a FastAPI service, containerize it with Docker, and evaluate latency vs batch size.\n2. Deep mathematical foundations: Be prepared to derive backpropagation for Transformers, explain attention mechanisms from scratch, and analyze loss functions.\n3. Knowledge of model quantization, LoRA fine-tuning, and inference optimization.\n\nFeel free to ask questions in the comments below!', 29, 2, FALSE, 'PUBLISHED'),
(3, 2, 'Top 10 Behavioral and HR Questions for Tech Campus Placements', 'HR Interviews', 'All Companies', 'Campus Placements', 'Technical skills get you to the interview table, but communication and emotional maturity seal the offer.\n\nHere are the top 5 questions you must prepare with the STAR method (Situation, Task, Action, Result):\n1. "Tell me about a complex project where something went wrong and how you recovered."\n2. "How do you prioritize when juggling deadlines for coursework and competitive programming?"\n3. "Describe a situation where you had to work with someone difficult."\n4. "Why this company instead of other competitors in your campus drive?"\n5. "What is an area of constructive feedback you received recently?"\n\nAlways prepare 2 thoughtful questions to ask the interviewer at the end!', 19, 1, FALSE, 'PUBLISHED')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert Demo Comments
INSERT INTO post_comments (id, post_id, author_id, content) VALUES
(1, 1, 4, 'Thank you Priya ma''am! Was the LRU Cache question asked to be implemented from scratch using a doubly linked list and hash map?'),
(2, 1, 2, 'Yes Aravind! Always write the doubly linked list node implementation manually first, rather than using built-in LinkedHashMap, unless explicitly permitted.'),
(3, 2, 5, 'Great advice Rohit sir! Should we include weights checkpoints on HuggingFace Hub as proof of training?'),
(4, 2, 3, 'Absolutely Sneha! Linking a HuggingFace Model Card with demo spaces adds immediate credibility.')
ON DUPLICATE KEY UPDATE content=VALUES(content);
