package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.EventType;
import com.satsuware.usefulviews.LabelledSpinner;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventCreateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCreateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventCreateFragment newInstance(String param1, String param2) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_create, container, false);
//        ClickToSelectEditText<EventType> ctseEventType = (ClickToSelectEditText<EventType>) view.findViewById(R.id.input_event_type);
//        ctseEventType.setItems(Arrays.asList(EventType.values()));
//        ctseEventType.setOnItemSelectedListener(new ClickToSelectEditText.OnItemSelectedListener<EventType>() {
//            @Override
//            public void onItemSelectedListener(EventType item, int selectedIndex) {
//
//            }
//        });

        LabelledSpinner eventTypeSpinnner = (LabelledSpinner) view.findViewById(R.id.labelled_spinner_event_type);
        eventTypeSpinnner.setItemsArray(Arrays.asList(EventType.values()));
        eventTypeSpinnner.setColor(android.R.color.tertiary_text_dark); // todo: check color
//        eventTypeSpinnner.getDivider().setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
