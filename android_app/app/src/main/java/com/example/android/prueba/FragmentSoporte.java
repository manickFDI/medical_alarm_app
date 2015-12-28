package com.example.android.prueba;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSoporte.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSoporte#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSoporte extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSoporte.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSoporte newInstance(String param1, String param2) {
        FragmentSoporte fragment = new FragmentSoporte();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSoporte() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_soporte, container, false);

        String[] items = new String[] { "Preguntas frecuentes", "Enviar correo", "Términos de uso",
                "Política de privacidad", "Avisos legales", "Versión actual"};

        ListView lista = (ListView) view.findViewById(R.id.lista_soporte);
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, items);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //preguntas frecuentes
                        /*Fragment_cardInfo fragment = new Fragment_cardInfo();
                        FragmentManager manager = getFragmentManager();
                        manager
                                .beginTransaction()
                                .replace(R.id.lista_soporte, fragment)
                                .commit();*/
                        Intent intent = new Intent(getContext(), Fragment_cardInfo.class);
                        Toast toast = Toast.makeText(getActivity(), "Toast por defecto -> No implementado", Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        break;
                    case 1:// enviar correo
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
                        mBuilder.setSmallIcon(android.R.drawable.sym_def_app_icon);
                        //mBuilder.setLargeIcon((((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.fdi_opt, null))));
                        mBuilder.setContentTitle("Mensaje enviado");
                        mBuilder.setContentText("Su mensaje ha sido enviado correctamente");
                        //mBuilder.setContentInfo("1");
                        mBuilder.setTicker("Enviado!");
                        mBuilder.setAutoCancel(true);
                        Intent notIntent = new Intent(getActivity(), MainActivity.class);
                        PendingIntent contIntent = PendingIntent.getActivity(getActivity(), 0, notIntent, 0);
                        mBuilder.setContentIntent(contIntent);
                        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text") ;
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"manuma02@ucm.es"});
                        i.putExtra(Intent.EXTRA_SUBJECT,"asunto del mensaje");
                        i.putExtra(Intent.EXTRA_TEXT,"cuerpo del mensaje");
                        startActivity(Intent.createChooser(i, "Seleccionar aplicación"));
                        break;
                    case 2://terminos de uso
                        String textT = "Este tratado se ha redactado en castellano. Dicha app ayudará a la localización de posibles contagios " +
                                "destacando que el contenido no es 100% fiable y sirve como soporte y ayuda para la sanida pública";
                        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                        DialogoInfo dialogo1 = new DialogoInfo();
                        dialogo1.setText(textT);
                        dialogo1.setTitle("Términos de uso");
                        dialogo1.show(fragmentManager1, "tagInfo");
                        break;
                    case 3: //politica de privacidad
                        String textP = "Toda la información recogida por esta app pertenecerá al estado español, el cual mantiene el anonimato" +
                                "y es responsable de su acceso y manipulación de los datos. Nadie tendrá acceso a información personal. Se cumple la LOPD.";
                        FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                        DialogoInfo dialogo2 = new DialogoInfo();
                        dialogo2.setText(textP);
                        dialogo2.setTitle("Política de privacidad");
                        dialogo2.show(fragmentManager2, "tagInfo");
                        break;
                    case 4: //avisos legales
                        String textA = "Al descargar esta app, acepta la política de privacidad y el uso del gps para su localización." +
                                "Toda información relevante en la app es informativa, en ningún caso es 100% fiable.";
                        FragmentManager fragmentManager3 = getActivity().getSupportFragmentManager();
                        DialogoInfo dialogo3 = new DialogoInfo();
                        dialogo3.setText(textA);
                        dialogo3.setTitle("Avisos legales");
                        dialogo3.show(fragmentManager3, "tagInfo");
                        break;
                    case 5: //version actual
                        Toast toast1 = Toast.makeText(getActivity(), "Versión actual: 1.0.0", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
