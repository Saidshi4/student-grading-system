export const NAV_ITEMS = [
  { path: '/', key: 'dashboard', roles: ['ADMIN', 'TEACHER', 'STUDENT', 'USER'] },
  { path: '/students', key: 'students', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/teachers', key: 'teachers', roles: ['ADMIN', 'TEACHER'] },
  { path: '/users', key: 'users', roles: ['ADMIN'] },
  { path: '/groups', key: 'groups', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/subjects', key: 'subjects', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/semesters', key: 'semesters', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/course-offerings', key: 'courseOfferings', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/class-schedules', key: 'classSchedules', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/enrollments', key: 'enrollments', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/grades', key: 'grades', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { path: '/roles', key: 'roles', roles: ['ADMIN'] },
  { path: '/claims', key: 'claims', roles: ['ADMIN'] },
  { path: '/menus', key: 'menus', roles: ['ADMIN'] },
  { path: '/matrix', key: 'matrix', roles: ['ADMIN'] },
  { path: '/profile', key: 'profile', roles: ['ADMIN', 'TEACHER', 'STUDENT', 'USER'] },
  { path: '/device-token', key: 'deviceToken', roles: ['ADMIN', 'TEACHER', 'STUDENT', 'USER'] },
  { path: '/auth-tools', key: 'authTools', roles: ['ADMIN', 'TEACHER', 'STUDENT', 'USER'] }
];

export const RESOURCES = {
  students: {
    path: '/students',
    titleKey: 'students',
    columns: ['id', 'firstName', 'lastName', 'username', 'email', 'birthDate', 'groupId', 'groupName'],
    filters: ['firstName', 'lastName', 'username', 'name', 'groupId'],
    fields: [
      { name: 'firstName', type: 'text' },
      { name: 'lastName', type: 'text' },
      { name: 'email', type: 'email' },
      { name: 'birthDate', type: 'date' },
      { name: 'groupId', type: 'number' }
    ]
  },
  teachers: {
    path: '/teachers',
    titleKey: 'teachers',
    columns: ['id', 'firstName', 'lastName', 'username', 'email', 'birthDate', 'department'],
    fields: [
      { name: 'firstName', type: 'text' },
      { name: 'lastName', type: 'text' },
      { name: 'email', type: 'email' },
      { name: 'birthDate', type: 'date' },
      { name: 'department', type: 'text' }
    ]
  },
  users: {
    path: '/users',
    titleKey: 'users',
    columns: ['id', 'firstName', 'lastName', 'fullName', 'username', 'email', 'phoneNumber', 'birthDate', 'role', 'imageUrl'],
    fields: [
      { name: 'firstName', type: 'text' },
      { name: 'lastName', type: 'text' },
      { name: 'email', type: 'email' },
      { name: 'role', type: 'text', createOnly: true },
      { name: 'birthDate', type: 'date' },
      { name: 'groupId', type: 'number', createOnly: true },
      { name: 'department', type: 'text', createOnly: true }
    ]
  },
  groups: {
    path: '/groups',
    titleKey: 'groups',
    columns: ['id', 'name', 'code', 'program', 'year'],
    fields: [
      { name: 'name', type: 'text' },
      { name: 'code', type: 'text' },
      { name: 'program', type: 'text' },
      { name: 'year', type: 'text' }
    ]
  },
  subjects: {
    path: '/subjects',
    titleKey: 'subjects',
    columns: ['id', 'name', 'code', 'description', 'credits'],
    fields: [
      { name: 'name', type: 'text' },
      { name: 'code', type: 'text' },
      { name: 'description', type: 'text' },
      { name: 'credits', type: 'number' }
    ]
  },
  semesters: {
    path: '/semesters',
    titleKey: 'semesters',
    columns: ['id', 'academicYear', 'semesterType', 'startDate', 'endDate'],
    fields: [
      { name: 'academicYear', type: 'text' },
      { name: 'semesterType', type: 'select', options: ['FALL', 'SPRING', 'SUMMER'] },
      { name: 'startDate', type: 'date' },
      { name: 'endDate', type: 'date' }
    ]
  },
  courseOfferings: {
    path: '/course-offerings',
    titleKey: 'courseOfferings',
    columns: ['id', 'capacity', 'status', 'enrollmentType', 'teacherId', 'teacherUsername', 'subjectId', 'subjectName', 'semesterId', 'academicYear', 'groupId', 'groupName'],
    filters: ['teacherId', 'semesterId', 'groupId'],
    fields: [
      { name: 'capacity', type: 'number' },
      { name: 'status', type: 'select', options: ['ACTIVE', 'INACTIVE'] },
      { name: 'enrollmentType', type: 'select', options: ['MANDATORY', 'ELECTIVE'] },
      { name: 'teacherId', type: 'number' },
      { name: 'subjectId', type: 'number' },
      { name: 'semesterId', type: 'number' },
      { name: 'groupId', type: 'number' }
    ]
  },
  classSchedules: {
    path: '/class-schedules',
    titleKey: 'classSchedules',
    columns: ['id', 'day', 'startTime', 'endTime', 'room', 'courseOfferingId'],
    filters: ['courseOfferingId'],
    fields: [
      { name: 'day', type: 'select', options: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] },
      { name: 'startTime', type: 'text' },
      { name: 'endTime', type: 'text' },
      { name: 'room', type: 'text' },
      { name: 'courseOfferingId', type: 'number' }
    ]
  },
  enrollments: {
    path: '/enrollments',
    titleKey: 'enrollments',
    columns: ['id', 'studentId', 'studentUsername', 'courseOfferingId', 'subjectName', 'status', 'enrolledAt', 'finalScore'],
    filters: ['studentId', 'courseOfferingId'],
    fields: [
      { name: 'studentId', type: 'number' },
      { name: 'courseOfferingId', type: 'number' },
      { name: 'status', type: 'select', options: ['ENROLLED', 'DROPPED'] }
    ]
  },
  grades: {
    path: '/grades',
    titleKey: 'grades',
    columns: ['id', 'score', 'type', 'enrollmentId', 'studentUsername', 'createdBy', 'updatedBy', 'createdAt', 'updatedAt'],
    filters: ['enrollmentId'],
    fields: [
      { name: 'score', type: 'number' },
      { name: 'type', type: 'select', options: ['MIDTERM', 'ASSIGNMENT', 'QUIZ', 'FINAL'] },
      { name: 'enrollmentId', type: 'number' }
    ]
  },
  roles: {
    path: '/roles',
    titleKey: 'roles',
    columns: ['id', 'name'],
    updateInBodyId: true,
    createResponseText: true,
    fields: [{ name: 'name', type: 'text' }]
  }
};
