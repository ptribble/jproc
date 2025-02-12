/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

#include <jni.h>
#include <procfs.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <pwd.h>
#include <grp.h>
#include <project.h>
#include <zone.h>

#include "uk_co_petertribble_jproc_api_NativeJProc.h"

/*
 * The class and method IDs are cached. We don't want to get them
 * on every call.
 */
static boolean_t jp_ids_cached;
static jclass jpi_class;
static jclass jpli_class;
static jclass jpu_class;
static jclass jps_class;
static jclass jpls_class;
static jmethodID jpi_constructor_mid;
static jmethodID jpli_constructor_mid;
static jmethodID jpu_constructor_mid;
static jmethodID jps_constructor_mid;
static jmethodID jpls_constructor_mid;
static jmethodID jpi_insert_mid;
static jmethodID jpli_insert_mid;
static jmethodID jpu_insert_mid;
static jmethodID jps_insert_mid;
static jmethodID jpls_insert_mid;

/*
 * A note on fixed sizes. Solaris defines PID_MAX to be 999999 which is 6
 * characters long. So /proc/pid/psinfo can be at most 20 characters long
 * including the trailing null. However, I'm going to allow 32-bit, or 10
 * character, pids, so the space required is 24 characters.
 *
 * The space required for /proc/pid/lwp/lwpid/lwpstatus is rather larger,
 * at 42 characters
 */

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getStatus
 * Signature: (I)Luk/co/petertribble/jproc/api/JProcStatus;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getStatus
  (JNIEnv *env, jobject jobj, jint pid)
{
  jobject jps;
  struct pstatus ps;
  int fd;
  char filename[24];

  sprintf(filename, "/proc/%i/status", pid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &ps, sizeof(pstatus_t)) != sizeof(pstatus_t)) {
    close(fd);
    return(NULL);
  }
  jps = (*env)->NewObject(env, jps_class, jps_constructor_mid);
  (*env)->CallVoidMethod(env, jps, jps_insert_mid, (jint)ps.pr_pid,
		(jlong)ps.pr_utime.tv_sec, (jlong)ps.pr_utime.tv_nsec,
		(jlong)ps.pr_stime.tv_sec, (jlong)ps.pr_stime.tv_nsec,
		(jlong)ps.pr_cutime.tv_sec, (jlong)ps.pr_cutime.tv_nsec,
		(jlong)ps.pr_cstime.tv_sec, (jlong)ps.pr_cstime.tv_nsec);
  close(fd);
  return (jps);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getLwpStatus
 * Signature: (II)Luk/co/petertribble/jproc/api/JProcLwpStatus;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getLwpStatus
  (JNIEnv *env, jobject jobj, jint pid, jint lwpid)
{
  jobject jpls;
  struct lwpstatus lps;
  int fd;
  char filename[42];

  sprintf(filename, "/proc/%i/lwp/%i/lwpstatus", pid, lwpid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &lps, sizeof(lwpstatus_t)) != sizeof(lwpstatus_t)) {
    close(fd);
    return(NULL);
  }
  jpls = (*env)->NewObject(env, jpls_class, jpls_constructor_mid);
  (*env)->CallVoidMethod(env, jpls, jpls_insert_mid, pid, (jint)lps.pr_lwpid,
		(jlong)lps.pr_utime.tv_sec, (jlong)lps.pr_utime.tv_nsec,
		(jlong)lps.pr_stime.tv_sec, (jlong)lps.pr_stime.tv_nsec);
  close(fd);
  return (jpls);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getInfo
 * Signature: (I)Luk/co/petertribble/jproc/api/JProcInfo;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getInfo
  (JNIEnv *env, jobject jobj, jint pid)
{
  jobject jpi;
  struct psinfo psi;
  int fd;
  char filename[24];
  jstring jfname;

  sprintf(filename, "/proc/%i/psinfo", pid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &psi, sizeof(psinfo_t)) != sizeof(psinfo_t)) {
    close(fd);
    return(NULL);
  }

  jfname = (*env)->NewStringUTF(env, psi.pr_fname);
  jpi = (*env)->NewObject(env, jpi_class, jpi_constructor_mid);
  (*env)->CallVoidMethod(env, jpi, jpi_insert_mid, (jint)psi.pr_pid,
		(jint)psi.pr_ppid, (jint)psi.pr_uid, (jint)psi.pr_euid,
		(jint)psi.pr_gid, (jint)psi.pr_egid,
		(jint)psi.pr_nlwp, (jlong)psi.pr_size, (jlong)psi.pr_rssize,
		(jlong)psi.pr_start.tv_sec, (jlong)psi.pr_time.tv_sec,
		(jlong)psi.pr_time.tv_nsec, (jlong)psi.pr_ctime.tv_sec,
		(jlong)psi.pr_ctime.tv_nsec, (jint)psi.pr_taskid,
		(jint)psi.pr_projid, (jint)psi.pr_zoneid,
		(jint)psi.pr_contract, jfname);
  (*env)->DeleteLocalRef(env, jfname);
  close(fd);
  return (jpi);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getLwpInfo
 * Signature: (II)Luk/co/petertribble/jproc/api/JProcLwpInfo;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getLwpInfo
  (JNIEnv *env, jobject jobj, jint pid, jint lwpid)
{
  jobject jpli;
  struct lwpsinfo lpsi;
  int fd;
  char filename[42];

  sprintf(filename, "/proc/%i/lwp/%i/lwpsinfo", pid, lwpid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &lpsi, sizeof(lwpsinfo_t)) != sizeof(lwpsinfo_t)) {
    close(fd);
    return(NULL);
  }
  jpli = (*env)->NewObject(env, jpli_class, jpli_constructor_mid);
  (*env)->CallVoidMethod(env, jpli, jpli_insert_mid, pid, (jint)lpsi.pr_lwpid,
		(jlong)lpsi.pr_start.tv_sec, (jlong)lpsi.pr_time.tv_sec,
		(jlong)lpsi.pr_time.tv_nsec);
  close(fd);
  return (jpli);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getUsage
 * Signature: (I)Luk/co/petertribble/jproc/api/JProcUsage;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getUsage
  (JNIEnv *env, jobject jobj, jint pid)
{
  jobject jpu;
  struct prusage pu;
  int fd;
  char filename[24];

  sprintf(filename, "/proc/%i/usage", pid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &pu, sizeof(prusage_t)) != sizeof(prusage_t)) {
    close(fd);
    return(NULL);
  }

  jpu = (*env)->NewObject(env, jpu_class, jpu_constructor_mid);
  (*env)->CallVoidMethod(env, jpu, jpu_insert_mid, (jint)pu.pr_lwpid,
		(jint)pu.pr_count,
		(jlong)pu.pr_rtime.tv_sec, (jlong)pu.pr_rtime.tv_nsec,
		(jlong)pu.pr_utime.tv_sec, (jlong)pu.pr_utime.tv_nsec,
		(jlong)pu.pr_stime.tv_sec, (jlong)pu.pr_stime.tv_nsec,
		(jlong)pu.pr_minf, (jlong)pu.pr_majf, (jlong)pu.pr_nswap,
		(jlong)pu.pr_inblk, (jlong)pu.pr_oublk,
		(jlong)pu.pr_msnd, (jlong)pu.pr_mrcv, (jlong)pu.pr_sigs,
		(jlong)pu.pr_vctx, (jlong)pu.pr_ictx,
		(jlong)pu.pr_sysc, (jlong)pu.pr_ioch);
  close(fd);
  return (jpu);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getLwpUsage
 * Signature: (II)Luk/co/petertribble/jproc/api/JProcUsage;
 */
JNIEXPORT jobject JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getLwpUsage
  (JNIEnv *env, jobject jobj, jint pid, jint lwpid)
{
  jobject jpu;
  struct prusage pu;
  int fd;
  char filename[42];

  sprintf(filename, "/proc/%i/lwp/%i/lwpusage", pid, lwpid);
  if ((fd = open(filename, O_RDONLY)) <= 0) {
    return(NULL);
  }

  if (read(fd, &pu, sizeof(prusage_t)) != sizeof(prusage_t)) {
    close(fd);
    return(NULL);
  }

  jpu = (*env)->NewObject(env, jpu_class, jpu_constructor_mid);
  (*env)->CallVoidMethod(env, jpu, jpu_insert_mid, (jint)pu.pr_lwpid,
		(jint)pu.pr_count,
		(jlong)pu.pr_rtime.tv_sec, (jlong)pu.pr_rtime.tv_nsec,
		(jlong)pu.pr_utime.tv_sec, (jlong)pu.pr_utime.tv_nsec,
		(jlong)pu.pr_stime.tv_sec, (jlong)pu.pr_stime.tv_nsec,
		(jlong)pu.pr_minf, (jlong)pu.pr_majf, (jlong)pu.pr_nswap,
		(jlong)pu.pr_inblk, (jlong)pu.pr_oublk,
		(jlong)pu.pr_msnd, (jlong)pu.pr_mrcv, (jlong)pu.pr_sigs,
		(jlong)pu.pr_vctx, (jlong)pu.pr_ictx,
		(jlong)pu.pr_sysc, (jlong)pu.pr_ioch);
  close(fd);
  return (jpu);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getUserName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getUserName
  (JNIEnv *env, jobject jobj, jint uid)
{
  struct passwd *pwd;
  jstring juname;

  pwd = getpwuid((uid_t) uid);
  if (!pwd) {
    return(NULL);
  }
  juname = (*env)->NewStringUTF(env, pwd->pw_name);
  return(juname);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getUserId
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getUserId
  (JNIEnv *env, jobject jobj, jstring jusername)
{
  struct passwd *pwd;
  const char *username;

  username = (*env)->GetStringUTFChars(env,jusername,NULL);
  pwd = getpwnam(username);
  (*env)->ReleaseStringUTFChars(env, jusername, username);
  if (!pwd) {
    return(-1);
  }
  return((jint) pwd->pw_uid);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getGroupName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getGroupName
  (JNIEnv *env, jobject jobj, jint gid)
{
  struct group *grp;
  jstring jgname;

  grp = getgrgid((gid_t) gid);
  if (!grp) {
    return(NULL);
  }
  jgname = (*env)->NewStringUTF(env, grp->gr_name);
  return(jgname);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getGroupId
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getGroupId
  (JNIEnv *env, jobject jobj, jstring jgroup)
{
  struct group *grp;
  const char *group;

  group = (*env)->GetStringUTFChars(env,jgroup,NULL);
  grp = getgrnam(group);
  (*env)->ReleaseStringUTFChars(env, jgroup, group);
  if (!grp) {
    return(-1);
  }
  return((jint) grp->gr_gid);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getProjectName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getProjectName
  (JNIEnv *env, jobject jobj, jint projid)
{
  struct project *proj;
  jstring jpname;
  ssize_t ret;
  char pname[PROJNAME_MAX];

  proj = getprojbyid((projid_t) projid, proj, pname, PROJNAME_MAX);
  if (!proj) {
    return(NULL);
  }
  jpname = (*env)->NewStringUTF(env, pname);
  return(jpname);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getProjectId
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getProjectId
  (JNIEnv *env, jobject jobj, jstring jproject)
{
  const char *project;
  projid_t proj;

  project = (*env)->GetStringUTFChars(env,jproject,NULL);
  proj = getprojidbyname(project);
  (*env)->ReleaseStringUTFChars(env, jproject, project);
  /* no need to check as getprojidbyname already returns -1 on failure */
  return((jint) proj);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getZoneName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getZoneName
  (JNIEnv *env, jobject jobj, jint zoneid)
{
  jstring jzname;
  ssize_t ret;
  char zname[ZONENAME_MAX];

  ret = getzonenamebyid((zoneid_t) zoneid, zname, ZONENAME_MAX);
  if (ret < 0) {
    return(NULL);
  }
  jzname = (*env)->NewStringUTF(env, zname);
  return(jzname);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    getZoneId
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_getZoneId
  (JNIEnv *env, jobject jobj, jstring jzone)
{
  const char *zone;
  ssize_t zoneid;

  zone = (*env)->GetStringUTFChars(env,jzone,NULL);
  zoneid = getzoneidbyname(zone);
  (*env)->ReleaseStringUTFChars(env, jzone, zone);
  /* no need to check as getzoneidbyname already returns -1 on failure */
  return((jint) zoneid);
}

/*
 * Class:     uk_co_petertribble_jproc_api_NativeJProc
 * Method:    cacheids
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_co_petertribble_jproc_api_NativeJProc_cacheids
  (JNIEnv *env, jclass class)
{
  jclass class_lref;

  class_lref = (*env)->FindClass(env, "uk/co/petertribble/jproc/api/JProcInfo");
  jpi_class = (*env)->NewGlobalRef(env, class_lref);
  jpi_constructor_mid = (*env)->GetMethodID(env, jpi_class, "<init>", "()V");
  jpi_insert_mid = (*env)->GetMethodID(env, jpi_class, "insert",
				       "(IIIIIIIJJJJJJJIIIILjava/lang/String;)V");

  class_lref = (*env)->FindClass(env, "uk/co/petertribble/jproc/api/JProcLwpInfo");
  jpli_class = (*env)->NewGlobalRef(env, class_lref);
  jpli_constructor_mid = (*env)->GetMethodID(env, jpli_class, "<init>", "()V");
  jpli_insert_mid = (*env)->GetMethodID(env, jpli_class, "insert",
				       "(IIJJJ)V");

  class_lref = (*env)->FindClass(env, "uk/co/petertribble/jproc/api/JProcUsage");
  jpu_class = (*env)->NewGlobalRef(env, class_lref);
  jpu_constructor_mid = (*env)->GetMethodID(env, jpu_class, "<init>", "()V");
  jpu_insert_mid = (*env)->GetMethodID(env, jpu_class, "insert",
				       "(IIJJJJJJJJJJJJJJJJJJ)V");

  class_lref = (*env)->FindClass(env, "uk/co/petertribble/jproc/api/JProcStatus");
  jps_class = (*env)->NewGlobalRef(env, class_lref);
  jps_constructor_mid = (*env)->GetMethodID(env, jps_class, "<init>", "()V");
  jps_insert_mid = (*env)->GetMethodID(env, jps_class, "insert",
				       "(IJJJJJJJJ)V");

  class_lref = (*env)->FindClass(env, "uk/co/petertribble/jproc/api/JProcLwpStatus");
  jpls_class = (*env)->NewGlobalRef(env, class_lref);
  jpls_constructor_mid = (*env)->GetMethodID(env, jpls_class, "<init>", "()V");
  jpls_insert_mid = (*env)->GetMethodID(env, jpls_class, "insert",
				       "(IIJJJJ)V");
}
