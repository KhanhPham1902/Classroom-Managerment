package com.khanhpham.managerclassroom.models;

import android.content.Context;

import com.khanhpham.managerclassroom.R;

import java.util.ArrayList;
import java.util.List;

public class ListSubject {
    public static List<Subject> getSubjectList(Context context){
        List<Subject> subjectList = new ArrayList<>();

        Subject thdtcb = new Subject();
        thdtcb.setSubject(context.getString(R.string.thdtcb));
        subjectList.add(thdtcb);

        Subject ltn = new Subject();
        ltn.setSubject(context.getString(R.string.ltn));
        subjectList.add(ltn);

        Subject ctdl = new Subject();
        ctdl.setSubject(context.getString(R.string.ctdl));
        subjectList.add(ctdl);

        Subject dts = new Subject();
        dts.setSubject(context.getString(R.string.dts));
        subjectList.add(dts);

        Subject thdttt = new Subject();
        thdttt.setSubject(context.getString(R.string.thdttt));
        subjectList.add(thdttt);

        Subject python = new Subject();
        python.setSubject(context.getString(R.string.python));
        subjectList.add(python);

        Subject cad = new Subject();
        cad.setSubject(context.getString(R.string.cad));
        subjectList.add(cad);

        Subject xlths = new Subject();
        xlths.setSubject(context.getString(R.string.xlths));
        subjectList.add(xlths);

        Subject dacs = new Subject();
        dacs.setSubject(context.getString(R.string.dacs));
        subjectList.add(dacs);

        Subject mmt = new Subject();
        mmt.setSubject(context.getString(R.string.mmt));
        subjectList.add(mmt);

        Subject vxl = new Subject();
        vxl.setSubject(context.getString(R.string.vxl));
        subjectList.add(vxl);

        Subject vdk = new Subject();
        vdk.setSubject(context.getString(R.string.vdk));
        subjectList.add(vdk);

        Subject dldkmt = new Subject();
        dldkmt.setSubject(context.getString(R.string.dldkmt));
        subjectList.add(dldkmt);

        Subject tkmtn = new Subject();
        tkmtn.setSubject(context.getString(R.string.tkmtn));
        subjectList.add(tkmtn);

        Subject tkdd = new Subject();
        tkdd.setSubject(context.getString(R.string.tkdd));
        subjectList.add(tkdd);

        Subject dacn = new Subject();
        dacn.setSubject(context.getString(R.string.dacn));
        subjectList.add(dacn);

        Subject tkhtn = new Subject();
        tkhtn.setSubject(context.getString(R.string.tkhtn));
        subjectList.add(tkhtn);

        Subject hdl = new Subject();
        hdl.setSubject(context.getString(R.string.hdl));
        subjectList.add(hdl);

        Subject ltntu = new Subject();
        ltntu.setSubject(context.getString(R.string.ltntu));
        subjectList.add(ltntu);

        Subject ltm = new Subject();
        ltm.setSubject(context.getString(R.string.ltm));
        subjectList.add(ltm);

        Subject tkmnm = new Subject();
        tkmnm.setSubject(context.getString(R.string.tkmnm));
        subjectList.add(tkmnm);

        Subject hmnd = new Subject();
        hmnd.setSubject(context.getString(R.string.hmnd));
        subjectList.add(hmnd);

        Subject xla = new Subject();
        xla.setSubject(context.getString(R.string.xla));
        subjectList.add(xla);

        Subject datn = new Subject();
        datn.setSubject(context.getString(R.string.datn));
        subjectList.add(datn);

        // No subject in list
        Subject other = new Subject();
        other.setSubject(context.getString(R.string.others));
        subjectList.add(other);

        return subjectList;
    }
}
