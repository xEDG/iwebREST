/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.service;

import entities.Serie;
import entities.Usuario;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import session.SessionBean;

/**
 *
 * @author user
 */
@Stateless
@Path("entities.usuario")
public class UsuarioFacadeREST extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "B6servidorRESTPU")
    private EntityManager em;

    @Inject
    private SessionBean smb;

    public UsuarioFacadeREST() {
        super(Usuario.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Usuario entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Usuario entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @GET
    @Path("usuarioPorEmail/{email}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario findConEmail(@PathParam("email") String email) {

        Query q;
        q = this.em.createQuery("SELECT u FROM Usuario u WHERE UPPER(u.email) = UPPER(:clave)");
        q.setParameter("clave", email);

        if (q.getResultList().size() > 0) {
            Usuario result = (Usuario) q.getResultList().get(0);

            return result;
        } else {
            return null;
        }
    }

    @GET
    @Path("boolSerieDeUsuario/{idSerie}")
    @Produces({MediaType.TEXT_PLAIN})
    public String findBoolSerieDeUsuario(@PathParam("idSerie") String idSerie) {
        System.out.println("IDSerie: " + idSerie + ".");
        if (!idSerie.equals("") && !idSerie.equals("{inputId}")) {
            
            Usuario u = this.findUsuarioConectado();

            if (u.getEmail().equals("pruebaparaingweb@gmail.com")) {
                System.out.println("Usuario con privilegios especiales: " + u.getEmail());
                return "ok";

            } else {

                Query q;
                q = this.em.createQuery("SELECT s FROM Serie s LEFT OUTER JOIN s.hasUsuarioCollection h WHERE s.id = (:clave2) AND h.idUsuario.email = (:clave)");

                q.setParameter("clave", u.getEmail());
                q.setParameter("clave2", Integer.parseInt(idSerie));

                if (q.getResultList().size() > 0) {
                    System.out.println("Bool true: " + q.getResultList().size());

                    return "ok";
                } else {
                    System.out.println("Bool false: :(");
                    return "notfound";
                }

            }
        } else {
            return "notfound";
        }
    }

    @GET
    @Path("usuarioConectado")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario findUsuarioConectado() {

        Query q;
        q = this.em.createQuery("SELECT u FROM Usuario u WHERE u.conectado = 1");

        if (q.getResultList().size() > 0) {
            Usuario result = (Usuario) q.getResultList().get(0);

            return result;
        } else {
            return null;
        }
    }

    @GET
    @Path("seriesDeUsuario/{email}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Serie> findSeriesDeUsuario(@PathParam("email") String email) {

        //unlog all
        Query q;
        q = this.em.createQuery("SELECT s FROM Serie s LEFT OUTER JOIN s.hasUsuarioCollection h WHERE h.idUsuario.email = (:clave)");
        q.setParameter("clave", email);

        if (q.getResultList().size() > 0) {
            System.out.println("Size lista series usuario: " + q.getResultList().size());
            List<Serie> lista = (List<Serie>) q.getResultList();

            return lista;
        } else {
            System.out.println("Size lista series usuario nula");
            return null;
        }
    }

    @POST
    @Path("/welcome")
    @Produces(MediaType.TEXT_PLAIN)
    public String login(UsuarioProxy proxy, @Context HttpServletRequest req) {

        //unlog all
        Query q;
        q = this.em.createQuery("UPDATE Usuario u SET u.conectado = 0");
        q.executeUpdate();

        Usuario u = this.findConEmail(proxy.email);
        if (u != null) {
            //actualizo la información del usuario existente
            System.out.println("Usuario encontrado, actualizando.");
            u.setNombre(proxy.name);
            u.setUrlFoto(proxy.imageUrl);
            u.setEmail(proxy.email);
            u.setConectado(1);
            this.edit(u);

        } else {
            System.out.println("Usuario no encontrado, creando.");
            Usuario nuevo = new Usuario();
            //No podemos setear la id. La id se autogenera
            //nuevo.setId(proxy.id);
            nuevo.setNombre(proxy.name);
            nuevo.setUrlFoto(proxy.imageUrl);
            nuevo.setEmail(proxy.email);
            nuevo.setConectado(1);

            this.create(nuevo);
        }

        return "ok";
    }

    public static class UsuarioProxy implements Serializable {

        public int id;
        public String name;
        public String imageUrl;
        public String email;

        public UsuarioProxy(int id, String name, String imageUrl, String email, String token) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
            this.email = email;

        }

        public UsuarioProxy() {
            // Nada, ha habido alguna cosa rara al pasar los parámetros.
        }

        UsuarioProxy(Usuario u) {
            this.id = u.getId();
            this.name = u.getNombre();
            this.imageUrl = u.getUrlFoto();
            this.email = u.getEmail();

        }

    }

    /*
    @POST
    @Path("/preferencias")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CategoriaProxy> nuevaPreferenciaUsuario(CategoriaProxy categoria, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException {
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        categoriaFacade.afegirPreferenciaUsuari(usuario, categoria.id);
        return categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }
     */
 /*id: profile.getId(),
      name: profile.getName(),
      imageUrl: profile.getImageUrl(),
      email: profile.getEmail(),
      token: googleUser.getAuthResponse().id_token,*/
    // /loadseries
    @POST
    @Path("/loadseries")
    @Produces(MediaType.TEXT_PLAIN)
    public String loadSeries(ProxySession proxy, @Context HttpServletRequest req) {

        System.out.println("Hola voy a imprimir el token.");
        System.out.println("Token: " + proxy.email);
        smb.setEmail(proxy.email);
        //teoría, creo que el set del atributo se tiene que hacer desde dentro del session.
        //crear una función y llamarla que le ponga el token que le pasen por parametros.
        System.out.println("Token after: " + proxy.email);
        System.out.println("Token session: " + smb.getEmail());

        return smb.getEmail();
    }

    public static class ProxySession implements Serializable {

        public String email;

        public ProxySession(String email) {
            this.email = email;

        }

        public ProxySession() {
            //Tiene que existir, no borrar.
        }

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String gimmeMail() {

        /*if (smb.getEmail() == null) {
           System
       }*/
        return smb.getEmail();
    }

    /*
    @POST
    @Path("/preferencias")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CategoriaProxy> nuevaPreferenciaUsuario(CategoriaProxy categoria, @HeaderParam("bearer") String token) throws NotAuthenticatedException, AgendamlgNotFoundException {
        String idUsuario = TokensUtils.getUserIdFromJwtTokenOrThrow(TokensUtils.decodeJwtToken(token));
        Usuario usuario = usuarioFacade.find(idUsuario);
        categoriaFacade.afegirPreferenciaUsuari(usuario, categoria.id);
        return categoriaFacade.buscarPreferenciasUsuario(usuario).stream().map(CategoriaProxy::new).collect(Collectors.toList());
    }
     */
 /*id: profile.getId(),
      name: profile.getName(),
      imageUrl: profile.getImageUrl(),
      email: profile.getEmail(),
      token: googleUser.getAuthResponse().id_token,*/
    @GET
    @Path("emailSerie/{idserie}")
    @Produces({MediaType.TEXT_PLAIN})
    public String findEmailDeSerie(@PathParam("idserie") String idserie) {

        Query q;
        //q = this.em.createQuery("SELECT u.email FROM Usuario u, HasUsuario h WHERE h.idSerie = (:clave) AND h.idUsuario = u.id");
        //+ "WHERE s.id = (:clave) AND h.idSerie = s.id AND h.idUsuario = u.id");
        //SELECT u.email FROM iweb.usuario u, iweb.serie s, iweb.has_usuario h 
        //WHERE s.id = (:clave) AND h.idSerie = s.id AND h.idUsuario = u.id;

        /*WHERE o.negocioCif IN (:negocioCif)

        use

        WHERE o.negocioCif.id in :collectionOfIdsOfNegocios
        
        select sl from Sensorlist sl // why select each and every field instead of selecting the entity?
        left outer join sl.sensorTagsCollection st
        where st.sensorId is null
         */
        q = this.em.createQuery("SELECT u.email FROM Usuario u LEFT OUTER JOIN u.hasUsuarioCollection h WHERE h.idSerie.id = (:clave)");

        int intIdSerie = Integer.parseInt(idserie);
        q.setParameter("clave", intIdSerie);

        if (q.getResultList().size() > 0) {
            String result = (String) q.getResultList().get(0);
            //cuando llega aquí peta.
            return result;
        } else {
            return "-1";
        }

    }

}
