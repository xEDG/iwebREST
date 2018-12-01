/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.service;

import entities.HasEntrega;
import entities.Entrega;
import entities.Serie;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Francis
 */
@Stateless
@Path("entities.hasentrega")
public class HasEntregaFacadeREST extends AbstractFacade<HasEntrega> {

    @PersistenceContext(unitName = "B6servidorRESTPU")
    private EntityManager em;

    public HasEntregaFacadeREST() {
        super(HasEntrega.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(HasEntrega entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, HasEntrega entity) {
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
    public HasEntrega find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<HasEntrega> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<HasEntrega> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    @Path("serieId/{serieId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Entrega> findEntregaConIdSerie(@PathParam("serieId") Integer serieId) {
        Query q;
        q = this.em.createQuery("SELECT h.idEntrega FROM HasEntrega h  WHERE h.idSerie.id = :clave");
        q.setParameter("clave", serieId);
        List<Entrega> entregas = (List) q.getResultList();
        return entregas;
    }
    
    @GET
    @Path("entregaId/{entregaId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Serie findSerieConEntrega(@PathParam("entregaId") Integer entregaId) {
       Query q;
        q = this.em.createQuery("SELECT h.idSerie FROM HasEntrega h  WHERE h.idEntrega.id = :clave");
        
        
        q.setParameter("clave", entregaId);
        Serie serie = (Serie) q.getSingleResult();
        return serie;
    }
}
